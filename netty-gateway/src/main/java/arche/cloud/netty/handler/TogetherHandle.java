package arche.cloud.netty.handler;

import java.util.Map;
import java.util.UUID;

import javax.net.ssl.SSLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import arche.cloud.netty.exceptions.Responsable;
import arche.cloud.netty.model.Backend;
import arche.cloud.netty.model.Constants;
import arche.cloud.netty.model.Route;
import arche.cloud.netty.model.User;
import arche.cloud.netty.model.UserRequest;
import arche.cloud.netty.utils.DataUtil;
import arche.cloud.netty.utils.RequestUtil;
import arche.cloud.netty.utils.ResponseUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.ReadTimeoutException;

public class TogetherHandle extends ChannelInboundHandlerAdapter {
  Logger logger = LoggerFactory.getLogger(TogetherHandle.class);

  private String reqId;
  private Channel outboundChannel;

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    cause.printStackTrace();
    closeOnFlush(ctx.channel());

    // logger.info(reqId);
    if (cause instanceof ReadTimeoutException) {
      // logger.error("pipeline timeout", cause);
      if (reqId != null) {
        ResponseUtil.wrap(ctx, HttpResponseStatus.GATEWAY_TIMEOUT,
            Map.of(Constants.HEADER_REQUEST_ID, reqId), "Gateway Timeout.");
      }
    }
    ctx.close();
  }

  @Override
  public void channelRead(final ChannelHandlerContext ctx, Object obj) throws InterruptedException {
    if (!(obj instanceof FullHttpRequest)) {
      logger.error("typeerror", "Incoming Message Is Not FullHttpRequest");
      return;
    }

    FullHttpRequest req = (FullHttpRequest) obj;
    // req.retain();

    UserRequest uq = RequestUtil.parse(req);
    // System.err.println(uq);
    reqId = UUID.randomUUID().toString();
    uq.setRequestId(reqId);
    try {
      String url = req.uri();
      // logger.info("current url: " + uq);

      // 这里是通用不受控 url，/favicon.ico 和 /metrics
      if (!CommonHandler.assertPathThrough(ctx, reqId, url)) {
        // req.release();
        ctx.close();
        return;
      }

      Route route = DataUtil.getRouteInfo(uq.getPath());

      // System.err.println(route);

      if (!CommonHandler.assertFirewall(ctx, route, reqId)) {
        // req.release();
        ctx.close();
        return;
      }
      if (CommonHandler.assertMock(ctx, route, reqId)) {
        // req.release();
        ctx.close();
        return;
      }
      // 这里限速没加上

      boolean publicRoute = DataUtil.isArrayEmpty(route.getAuthorizedRoles())
          && DataUtil.isArrayEmpty(route.getForbiddenRoles());

      User user = null;
      if (!publicRoute) {
        user = DataUtil.getUser(uq.getTicket());
        if (!CommonHandler.assertUserPass(ctx, route, user, reqId)) {
          // req.release();
          ctx.close();
          return;
        }
      }

      Backend backend = DataUtil.chooseBackend(route, uq);
      String query = uq.getQuery();
      String queryPath = uq.getPath();
      String routePath = route.getFullPath();
      String rpcUrl = backend.toUrl();
      if (routePath.endsWith("/")) {
        rpcUrl += queryPath.substring(routePath.length());
      }
      if (query != null) {
        rpcUrl += "?" + uq.getQuery();
      }

      logger.info(query);

      FullHttpRequest request = RequestUtil.copyRequest(req, rpcUrl);

      // req.release();

      request.headers().set(HttpHeaderNames.HOST, backend.getHost() + ":" + backend.getPort());
      request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
      request.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);
      if (user != null) {
        request.headers().set("__cloudaring_user__", user.toHeaderString());
      }
      request.headers().set("__request_id__", reqId);

      final Channel inboundChannel = ctx.channel();
      final boolean ssl = "https".equalsIgnoreCase(backend.getSchema());
      final SslContext sslCtx;
      if (ssl) {
        sslCtx = SslContext.newClientContext(InsecureTrustManagerFactory.INSTANCE);
      } else {
        sslCtx = null;
      }

      // System.out.println("SSL: " + sslCtx);

      EventLoopGroup group = new NioEventLoopGroup();
      try {
        Bootstrap b = new Bootstrap();
        b.group(group)
            .channel(NioSocketChannel.class)
            .handler(new RpcClientInitializer(sslCtx, inboundChannel, route, uq))
            .option(ChannelOption.AUTO_READ, false);

        outboundChannel = b.connect(backend.getHost(), backend.getPort()).sync().channel();
        outboundChannel.writeAndFlush(request);
        outboundChannel.closeFuture().sync();
        // System.out.println("in:" + inboundChannel);
        // System.out.println("out:" + outboundChannel);
      } finally {
        // Shut down executor threads to exit.
        group.shutdownGracefully();
      }

      // ChannelFuture f = b.connect(backentd.getHost(), backend.getPort());

      // outboundChannel = f.channel();

      // System.out.println("outboundChannel: " + outboundChannel);
      // System.out.println("isActive: " + outboundChannel.isActive());
      // System.out.println("user: " + user);

      // // outboundChannel.writeAndFlush(request).addListener(listener);
      // // outboundChannel.closeFuture().sync();

      // if (outboundChannel.isActive()) {
      // System.out.println("Hhhhhhh beigin write...");
      // outboundChannel.writeAndFlush(request).addListener(new
      // ChannelFutureListener() {
      // @Override
      // public void operationComplete(ChannelFuture future) {
      // if (future.isSuccess()) {
      // // connection complete start to read first data
      // inboundChannel.read();
      // } else {
      // // Close the connection if the connection attempt has failed.
      // inboundChannel.close();
      // }
      // }
      // });
      // }

    } catch (Responsable e) {
      // e.printStackTrace();
      e.echo(ctx, reqId, uq.logInfo(), false);
      req.release();
      ctx.close();
    } catch (SSLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) {
    if (outboundChannel != null) {
      // System.out.println("Close outbound");
      closeOnFlush(outboundChannel);
    }
  }

  /**
   * Closes the specified channel after all queued write requests are flushed.
   */
  static void closeOnFlush(Channel ch) {
    if (ch.isActive()) {
      ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }
  }

}
