package arche.cloud.netty.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import arche.cloud.netty.exceptions.Responsable;
import arche.cloud.netty.model.DataKeys;
import arche.cloud.netty.model.Route;
import arche.cloud.netty.model.UserRequest;
import arche.cloud.netty.utils.DataUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;

public class S2LoadRoute extends SimpleChannelInboundHandler<FullHttpRequest> {
  Logger logger = LoggerFactory.getLogger(S2LoadRoute.class);

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) {
    req.retain();
    UserRequest uq = ctx.channel().attr(DataKeys.REQUEST_INFO).get();

    try {
      Route route = DataUtil.getRouteInfo(uq.getPath());
      // RequestUtil.getApiInfo(uq.getPath(), uq.getMethod());

      ctx.channel().attr(DataKeys.API_INFO).set(route);
      ctx.fireChannelRead(req);
    } catch (Responsable e) {
      e.echo(ctx, uq.getRequestId(), uq.logInfo(), false);
      // e.echo(ctx, uq.getRequestId(), uq.logInfo());
    }
    // ctx.pipeline().remove(this);

  }
}