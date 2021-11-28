package arche.cloud.netty;

import arche.cloud.netty.handler.S2LoadRoute;
import arche.cloud.netty.handler.S1ParseRequest;
import arche.cloud.netty.handler.S3LoadUser;
import arche.cloud.netty.handler.S4Checker;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel channel) {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast(new HttpServerCodec());// http 编解码
        // http 消息聚合器
        // 512*1024为接收的最大contentlength
        pipeline.addLast("httpAggregator",
                new HttpObjectAggregator(512 * 1024)
        );

        pipeline.addLast(new S1ParseRequest());
        pipeline.addLast(new S2LoadRoute());
        pipeline.addLast(new S3LoadUser());
        pipeline.addLast(new S4Checker());


    }
}
