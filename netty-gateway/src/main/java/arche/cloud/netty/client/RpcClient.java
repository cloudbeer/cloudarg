package arche.cloud.netty.client;

import arche.cloud.netty.exceptions.Internal;
import arche.cloud.netty.exceptions.Responsable;
import arche.cloud.netty.model.*;
import arche.cloud.netty.utils.DataUtil;
import arche.cloud.netty.utils.RequestUtil;
import arche.cloud.netty.utils.StringUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
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

  private void accessMockUrlContent() {
    try {
      String mockUrl = route.getMockContentUrl();
      if (StringUtil.isBlank(mockUrl)) {
        throw new Internal("Mock url required. ");
      }
      URL urlMock = new URL(mockUrl);
      String host = urlMock.getHost();
      int port = urlMock.getPort();
      String schema = urlMock.getProtocol();
      if (port <= 0) {
        if ("http".equals(schema)) {
          port = 80;
        } else if ("https".equals(schema)) {
          port = 443;
        }
      }
      ColdChannelPool.BOOTSTRAP.remoteAddress(host, port);
      final FixedChannelPool pool = ColdChannelPool.POOLMAP.get(host + ":" + port);
      Future<Channel> future = pool.acquire();
      future.addListener((FutureListener<Channel>) channelFuture -> {
        if (future.isSuccess()) {
          Channel channel = future.getNow();
          channel.attr(DataKeys.PARENT_CONTEXT).set(parentContext);
          channel.attr(DataKeys.API_INFO).set(route);
          channel.attr(DataKeys.REQUEST_INFO).set(userRequest);

          FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, mockUrl);
          request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
          request.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);
          channel.write(request);
          channel.writeAndFlush(Unpooled.EMPTY_BUFFER);
          pool.release(channel);
        }
      });
    } catch (Responsable e) {
      e.echo(parentContext, userRequest.getRequestId(), userRequest.logInfo(), false);
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
  }

  public void accessBackend() {
    // if (route.getMock() == 2) {
    // accessMockUrlContent();
    // return;
    // }
    try {
      Backend backend = DataUtil.chooseBackend(route, userRequest);
      String host = backend.getHost();
      int port = backend.getPort();
      ColdChannelPool.BOOTSTRAP.remoteAddress(host, port);
      final FixedChannelPool pool = ColdChannelPool.POOLMAP.get(host + ":" + port);
      Future<Channel> future = pool.acquire();
      future.addListener((FutureListener<Channel>) channelFuture -> {
        if (future.isSuccess()) {
          Channel channel = future.getNow();
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

          FullHttpRequest request = RequestUtil.copyRequest(parentRequest, rpcUrl);
          System.err.println(rpcUrl);
          channel.attr(DataKeys.BACKEND).set(rpcUrl);

          request.headers().set(HttpHeaderNames.HOST, host + ":" + port);
          request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
          request.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);
          if (user != null) {
            request.headers().set("__cloudaring_user__", user.toHeaderString());
          }
          request.headers().set("__request_id__", userRequest.getRequestId());

          channel.write(request);
          channel.writeAndFlush(Unpooled.EMPTY_BUFFER);
          pool.release(channel);
        }
      });
    } catch (Responsable e) {
      e.echo(parentContext, userRequest.getRequestId(), userRequest.logInfo(), false);
    }
  }
}
