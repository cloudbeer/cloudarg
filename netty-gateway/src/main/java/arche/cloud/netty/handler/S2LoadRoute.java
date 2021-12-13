package arche.cloud.netty.handler;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import arche.cloud.netty.exceptions.Responsable;
import arche.cloud.netty.model.DataKeys;
import arche.cloud.netty.model.Route;
import arche.cloud.netty.model.UserRequest;
import arche.cloud.netty.ratelimit.BucketFactory;
import arche.cloud.netty.utils.DataUtil;
import arche.cloud.netty.utils.ResponseUtil;
import io.github.bucket4j.Bucket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;

public class S2LoadRoute extends SimpleChannelInboundHandler<FullHttpRequest> {
  Logger logger = LoggerFactory.getLogger(S2LoadRoute.class);

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) {
    req.retain();
    UserRequest uq = ctx.channel().attr(DataKeys.REQUEST_INFO).get();

    try {
      Route route = DataUtil.getRouteInfo(uq.getPath());

      int rateLimit = route.getRateLimit();
      if (rateLimit > 0) {
        Bucket bucket = BucketFactory.getBucket(route.getPath(), rateLimit);

        if (!bucket.tryConsume(1)) {
          String logInfo = uq.logInfo();
          logger.error("Out of qps[{}]: {}", rateLimit, logInfo);
          ResponseUtil.wrap(ctx, HttpResponseStatus.TOO_MANY_REQUESTS,
              Map.of("request_id", uq.getRequestId()), "Too many request.");
          req.release();
          return;
        }
      }

      ctx.channel().attr(DataKeys.API_INFO).set(route);
      ctx.fireChannelRead(req);
    } catch (Responsable e) {
      e.echo(ctx, uq.getRequestId(), uq.logInfo(), false);
      req.release();
    }
    // ctx.pipeline().remove(this);

  }
}