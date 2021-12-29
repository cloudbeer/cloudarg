package arche.cloud.netty.client;

import java.net.InetSocketAddress;
import arche.cloud.netty.exceptions.Internal;
import arche.cloud.netty.exceptions.Responsable;
import arche.cloud.netty.model.*;
import arche.cloud.netty.utils.DataUtil;
import arche.cloud.netty.utils.RequestUtil;
import arche.cloud.netty.utils.StringUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcClient {

  Logger logger = LoggerFactory.getLogger(RpcClient.class);
  private ChannelHandlerContext parentContext;
  private FullHttpRequest parentRequest;
  private UserRequest userRequest;
  private Route route;
  private User user;

  public ChannelHandlerContext getParentContext() {
    return parentContext;
  }

  public void setParentContext(ChannelHandlerContext parentContext) {
    this.parentContext = parentContext;
  }

  public UserRequest getUserRequest() {
    return userRequest;
  }

  public void setUserRequest(UserRequest userRequest) {
    this.userRequest = userRequest;
  }

  public Route getRoute() {
    return route;
  }

  public void setRoute(Route route) {
    this.route = route;
  }

  public FullHttpRequest getParentRequest() {
    return parentRequest;
  }

  public void setParentRequest(FullHttpRequest parentRequest) {
    this.parentRequest = parentRequest;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public RpcClient() {
    // TODO document why this constructor is empty
  }

  private void accessBackend(Channel channel, Backend backend) {

    channel.attr(DataKeys.PARENT_CONTEXT).set(parentContext);
    channel.attr(DataKeys.API_INFO).set(route);
    channel.attr(DataKeys.REQUEST_INFO).set(userRequest);

    String query = userRequest.getQuery();
    String queryPath = userRequest.getPath();
    String routePath = route.getFullPath();
    String rpcUrl = backend.toUrl();
    if (routePath.endsWith("/")) {
      rpcUrl += queryPath.substring(routePath.length());
    }
    if (query != null) {
      rpcUrl += "?" + userRequest.getQuery();
    }

    System.out.println(":" + channel + "--" + rpcUrl);

    // logger.info("remote-url: {}", rpcUrl);
    parentContext.channel().attr(DataKeys.BACKEND).set(rpcUrl);

    FullHttpRequest request = RequestUtil.copyRequest(parentRequest, rpcUrl);

    request.headers().set(HttpHeaderNames.HOST, backend.getHost() + ":" + backend.getPort());
    // request.headers().set(HttpHeaderNames.CONNECTION,
    // HttpHeaderValues.KEEP_ALIVE);
    request.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);
    if (user != null) {
      request.headers().set("__cloudaring_user__", user.toHeaderString());
    }
    request.headers().set("__request_id__", userRequest.getRequestId());

    channel.write(request);
    channel.flush();
  }

  public void accessBackend() {
    try {
      Backend backend = DataUtil.chooseBackend(route, userRequest);
      String host = backend.getHost();
      int port = backend.getPort();
      ColdChannelPool.BOOTSTRAP.remoteAddress(host, port);

      FixedChannelPool pool = ColdChannelPool.POOLMAP.get(new InetSocketAddress(host, port));
      // System.out.println("pool ---- " + pool);
      Future<Channel> future = pool.acquire();
      // ChannelFuture future = ColdChannelPool.BOOTSTRAP.connect(host,
      // port).syncUninterruptibly();

      // if (!future.isSuccess()) {
      // logger.error("connection error", future.cause());
      // } else {
      // Channel channel = future.channel();
      // accessBackend(channel, backend);
      // }
      // Future<Channel> future = pool.acquire().syncUninterruptibly();
      // if (!future.isSuccess()) {
      // logger.error("connection error", future.cause());
      // } else {
      // Channel channel = future.getNow();
      // accessBackend(channel, backend);
      // pool.release(channel);
      // }

      future.addListener((FutureListener<Channel>) channelFuture -> {
        if (channelFuture.isSuccess()) {
          // Channel channel = channelFuture.get(1, TimeUnit.SECONDS);
          Channel channel = channelFuture.getNow();
          accessBackend(channel, backend);
          pool.release(channel);
        }
      });
    } catch (Responsable e) {
      e.echo(parentContext, userRequest.getRequestId(), userRequest.logInfo(), false);
    }
  }
}
