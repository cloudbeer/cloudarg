package arche.cloud.netty;

import java.net.InetSocketAddress;

import arche.cloud.netty.config.ConfigFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class HttpServer {

    int port;

    public HttpServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {

        ConfigFactory.load();

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
                    .childHandler(new HttpServerInitializer())
                    .childOption(ChannelOption.AUTO_READ, true);

            ChannelFuture f = bootstrap.bind(new InetSocketAddress(port)).sync();
            System.out.println("Server start up on port : " + port);

            System.out.println();
            System.out.println("----------------------");
            System.out.println("Starting " + ConfigFactory.config.getName() + " server...");
            System.out.println("Gateway: " + ConfigFactory.config.getPort());
            System.out.println("Mysql  : " + ConfigFactory.config.getMysql().getHost());
            System.out.println(ConfigFactory.config.getDescription());
            System.out.println("----------------------");
            System.out.println();

            // System.out.println(CIDRUtil.toCidr("5.10.64.0", "5.10.254.255"));
            // System.out.println(Arrays.toString(CIDR6Util.toBigIntRange("5.10.64.0")));
            // System.out.println(Arrays.toString(CIDR6Util.toStringRange("192:168:0:1:0:0:0:0/120")));
            // System.out.println(Arrays.toString(CIDR6Util.toBigIntRange("0::0:0:0:0:1/128")));
            // System.out.println(
            // CIDR6Util.inRange("5.10.128.1", "5::10:128:0:0:0"));
            // System.out.println(CIDR6Util.inRange("192.168.0.1",
            // "192:168:0:1:0:0:0:0/120"));

            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

}
