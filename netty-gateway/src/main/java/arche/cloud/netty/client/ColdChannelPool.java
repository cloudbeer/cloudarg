package arche.cloud.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.AbstractChannelPoolMap;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.pool.ChannelPoolMap;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;

public class ColdChannelPool {
    public static ChannelPoolMap<String, FixedChannelPool> POOLMAP;
    public static final Bootstrap BOOTSTRAP = new Bootstrap();
    static {
        BOOTSTRAP.group(new NioEventLoopGroup());
        BOOTSTRAP.channel(NioSocketChannel.class);

        POOLMAP = new AbstractChannelPoolMap<>() {

            @Override
            protected FixedChannelPool newPool(String key) {
                ChannelPoolHandler handler = new ChannelPoolHandler() {
                    /**
                     * 使用完channel需要释放才能放入连接池
                     *
                     */
                    @Override
                    public void channelReleased(Channel ch) throws Exception {
                        // 刷新管道里的数据
//                        ch.writeAndFlush(Unpooled.EMPTY_BUFFER); // flush掉所有写回的数据
//                        ColdChannelPool.BOOTSTRAP.attr(ColdChannelPool.PARENT_CONTEXT, null);
//                        ColdChannelPool.BOOTSTRAP.attr(ColdChannelPool.API_INFO, null);
//                        System.out.println("channelReleased......");
                    }

                    /**
                     * 当链接创建的时候添加channelhandler，只有当channel不足时会创建，但不会超过限制的最大channel数
                     *
                     */
                    @Override
                    public void channelCreated(Channel ch) throws Exception {
                        ch.pipeline().addLast(new HttpClientCodec())
                                .addLast(new HttpContentDecompressor())
                                .addLast(new HttpObjectAggregator(10 * 1024 * 1024))
                                .addLast("rpcHandler", new RpcInboundHandler());

                    }

                    /**
                     *  获取连接池中的channel
                     *
                     */
                    @Override
                    public void channelAcquired(Channel ch) throws Exception {
//                        System.out.println(LocalDateTime.now() + " - channelAcquired......");
//                        System.out.println(ch.attr(ColdChannelPool.PARENT_CONTEXT));
                    }
                };

                return new FixedChannelPool(BOOTSTRAP, handler, 50); //单个host连接池大小
            }
        };
    }
}
