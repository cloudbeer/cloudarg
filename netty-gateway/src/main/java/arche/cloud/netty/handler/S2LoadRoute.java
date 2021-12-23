package arche.cloud.netty.handler;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import arche.cloud.netty.exceptions.Responsable;
import arche.cloud.netty.model.Constants;
import arche.cloud.netty.model.DataKeys;
import arche.cloud.netty.model.Route;
import arche.cloud.netty.model.UserRequest;
import arche.cloud.netty.ratelimit.BucketFactory;
import arche.cloud.netty.utils.CIDR6Util;
import arche.cloud.netty.utils.DataUtil;
import arche.cloud.netty.utils.ResponseUtil;
import io.github.bucket4j.Bucket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;

public class S2LoadRoute extends SimpleChannelInboundHandler<FullHttpRequest> {
  Logger logger = LoggerFactory.getLogger(S2LoadRoute.class);

  // @Override
  // public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

  // logger.error("pipeline error", cause);
  // if (cause instanceof ReadTimeoutException) {
  // UserRequest uq = ctx.channel().attr(DataKeys.REQUEST_INFO).get();
  // ResponseUtil.wrap(ctx, HttpResponseStatus.REQUEST_TIMEOUT,
  // Map.of(Constants.HEADER_REQUEST_ID, uq.getRequestId()), "Request timeout.");
  // }
  // ctx.close();
  // }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) {
    req.retain();
    UserRequest uq = ctx.channel().attr(DataKeys.REQUEST_INFO).get();

    InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
    InetAddress inetaddress = socketAddress.getAddress();
    String ipAddress = inetaddress.getHostAddress(); // IP address of client

    try {
      Route route = DataUtil.getRouteInfo(uq.getPath());

      if (route.getMock() > 0) {
        CommonHandler.echoMockContent(ctx, req, route);
        req.release();
        return;
      }
      // System.err.println(route);

      String[] whiteList = route.getWhiteList();
      boolean isInWhiteList;
      if (whiteList != null && whiteList.length > 0) {
        isInWhiteList = CIDR6Util.inRange(ipAddress, whiteList);
        if (!isInWhiteList) {
          String logInfo = uq.logInfo();
          logger.error("Client [{}] not in white list: {}", ipAddress, logInfo);
          ResponseUtil.wrap(ctx, HttpResponseStatus.NOT_ACCEPTABLE,
              Map.of(Constants.HEADER_REQUEST_ID, uq.getRequestId()), "Not acceptable.");
          req.release();
          return;
        }
      }

      String[] blackList = route.getBlackList();
      // if (blackList != null && blackList.length > 0 && !isInWhiteList) {
      if (blackList != null && blackList.length > 0) {
        boolean isInBlackList = CIDR6Util.inRange(ipAddress, blackList);
        if (isInBlackList) {
          String logInfo = uq.logInfo();
          logger.error("Client [{}] in black list: {}", ipAddress, logInfo);
          ResponseUtil.wrap(ctx, HttpResponseStatus.NOT_ACCEPTABLE,
              Map.of(Constants.HEADER_REQUEST_ID, uq.getRequestId()), "Not acceptable.");
          req.release();
          return;
        }
      }

      int rateLimit = route.getRateLimit();
      if (rateLimit > 0) {
        Bucket bucket = BucketFactory.getBucket(route.getPath(), rateLimit);

        if (!bucket.tryConsume(1)) {
          String logInfo = uq.logInfo();
          logger.error("Out of qps[{}]: {}", rateLimit, logInfo);
          ResponseUtil.wrap(ctx, HttpResponseStatus.TOO_MANY_REQUESTS,
              Map.of(Constants.HEADER_REQUEST_ID, uq.getRequestId()), "Too many request.");
          req.release();
          return;
        }
      }

      ctx.channel().attr(DataKeys.API_INFO).set(route);
      ctx.fireChannelRead(req);
    } catch (Responsable e) {
      // e.printStackTrace();
      logger.error("Catched errors: {}", uq.logInfo());
      e.echo(ctx, uq.getRequestId(), uq.logInfo(), false);
      req.release();
    }
    // ctx.pipeline().remove(this);

  }
}