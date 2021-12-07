package arche.cloud.netty;

import arche.cloud.netty.config.ConfigFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;

public class HttpServer {

    int port;

    public HttpServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        ConfigFactory.load();
        init();
    }

    public static void init() throws Exception {
        int port = ConfigFactory.config.getPort();
        new HttpServer(port).run();
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup(10);
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new HttpServerInitializer());

            ChannelFuture f = bootstrap.bind(new InetSocketAddress(port)).sync();
            System.out.println("Server start up on port : " + port);

            System.out.println();
            System.out.println("----------------------");
            System.out.println("Starting " + ConfigFactory.config.getName() + " server...");
            System.out.println("Gateway: " + ConfigFactory.config.getPort());
            System.out.println("Mysql  : " + ConfigFactory.config.getMysql().getHost());
            System.out.println("Redis  : " + ConfigFactory.config.getRedis().getHost());
            System.out.println(ConfigFactory.config.getDescription());
            System.out.println("----------------------");
            System.out.println();

            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

}
