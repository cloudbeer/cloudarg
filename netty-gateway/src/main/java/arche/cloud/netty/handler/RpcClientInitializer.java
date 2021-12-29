package arche.cloud.netty.handler;

import arche.cloud.netty.model.Route;
import arche.cloud.netty.model.UserRequest;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.ssl.SslContext;

public class RpcClientInitializer extends ChannelInitializer<SocketChannel> {

  private final SslContext sslCtx;

  private final Channel inboundChannel;
  private final Route route;
  private final UserRequest uq;

  public RpcClientInitializer(SslContext sslCtx, Channel inboundChannel, Route route, UserRequest uq) {
    this.sslCtx = sslCtx;
    this.inboundChannel = inboundChannel;
    this.route = route;
    this.uq = uq;
  }

  @Override
  public void initChannel(SocketChannel ch) {
    ChannelPipeline p = ch.pipeline();

    // Enable HTTPS if necessary.
    if (sslCtx != null) {
      p.addLast(sslCtx.newHandler(ch.alloc()));
    }

    p.addLast(new HttpClientCodec());

    // Remove the following line if you don't want automatic content decompression.
    p.addLast(new HttpContentDecompressor());

    // Uncomment the following line if you don't want to handle HttpContents.
    p.addLast(new HttpObjectAggregator(1048576));

    p.addLast(new RpcInboundHandle(inboundChannel, route, uq));
  }
}
