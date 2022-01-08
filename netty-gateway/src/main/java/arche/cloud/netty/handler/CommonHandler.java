package arche.cloud.netty.handler;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Map;

import arche.cloud.netty.model.Constants;
import arche.cloud.netty.model.Route;
import arche.cloud.netty.model.User;
import arche.cloud.netty.utils.CIDR6Util;
import arche.cloud.netty.utils.DataUtil;
import arche.cloud.netty.utils.ResponseUtil;
import arche.cloud.netty.utils.StringUtil;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CommonHandler {

  private CommonHandler() {
    throw new IllegalStateException("Utility class");
  }

  public static void echoMetrics(ChannelHandlerContext ctx, String reqId) {
    FullHttpResponse response = new DefaultFullHttpResponse(
        HttpVersion.HTTP_1_1,
        HttpResponseStatus.OK);
    response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
    response.headers().set(Constants.HEADER_REQUEST_ID, reqId);
    response.content().writeCharSequence("metrics in developing.", CharsetUtil.UTF_8);

    ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    // ctx.close();
  }

  public static void echoFavicon(ChannelHandlerContext ctx, String reqId) {
    FullHttpResponse response = new DefaultFullHttpResponse(
        HttpVersion.HTTP_1_1,
        HttpResponseStatus.OK);
    response.headers().set(HttpHeaderNames.CONTENT_TYPE, "image/x-icon");
    response.headers().set(Constants.HEADER_REQUEST_ID, reqId);
    InputStream is = CommonHandler.class.getClassLoader().getResourceAsStream("logo.ico");
    assert is != null;
    // InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
    try {
      response.content().writeBytes(is, is.available());
    } catch (IOException e) {
      e.printStackTrace();
    }
    ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    // ctx.close();
  }

  public static boolean assertPathThrough(ChannelHandlerContext ctx, String reqId, String url) {
    if ("/metrics".equals(url)) {
      CommonHandler.echoMetrics(ctx, reqId);
      return false;
    }
    if ("/favicon.ico".equals(url)) {
      CommonHandler.echoFavicon(ctx, reqId);
      return false;
    }
    return true;
  }

  /**
   * Assert current request can pass through the firewalls policy.
   * 
   * @param ctx
   * @param route
   * @param reqId
   * @return if pass return true, if block return false.
   */
  public static boolean assertFirewall(ChannelHandlerContext ctx, Route route, String reqId) {

    InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
    InetAddress inetaddress = socketAddress.getAddress();
    String ipAddress = inetaddress.getHostAddress();

    String[] whiteList = route.getWhiteList();
    boolean isInWhiteList;
    if (whiteList != null && whiteList.length > 0) {
      isInWhiteList = CIDR6Util.inRange(ipAddress, whiteList);
      if (!isInWhiteList) {
        ResponseUtil.wrap(ctx, HttpResponseStatus.NOT_ACCEPTABLE,
            Map.of(Constants.HEADER_REQUEST_ID, reqId), "Client IP Not In Acceptable List.");
        return false;
      }
    }

    String[] blackList = route.getBlackList();
    // if (blackList != null && blackList.length > 0 && !isInWhiteList) {
    if (blackList != null && blackList.length > 0) {
      boolean isInBlackList = CIDR6Util.inRange(ipAddress, blackList);
      if (isInBlackList) {
        ResponseUtil.wrap(ctx, HttpResponseStatus.NOT_ACCEPTABLE,
            Map.of(Constants.HEADER_REQUEST_ID, reqId), "Client IP  In Block List.");
        return false;
      }
    }
    return true;
  }

  public static boolean assertMock(ChannelHandlerContext ctx, Route route, String reqId) {
    int mock = route.getMock();
    if (mock != 1 && mock != 2) {
      return false;
    }
    FullHttpResponse response = new DefaultFullHttpResponse(
        HttpVersion.HTTP_1_1,
        HttpResponseStatus.OK);
    String contentType = null;

    if (route.getMock() == 1) {
      contentType = route.getMockContentType();
      if (StringUtil.isBlank(contentType)) {
        contentType = "text/html";
      }
      String content = route.getMockContent();
      if (StringUtil.isBlank(content)) {
        contentType = "Blank Content.";
      }

      if (route.getWrapper() == 1) {
        contentType = "application/json";
        content = "{\"service\":\"cloudarg\",\"success\":true,\"data\":" + content + "}";
      }

      response.content().writeCharSequence(content, CharsetUtil.UTF_8);

    } else if (route.getMock() == 2) {
      String url = route.getMockContentUrl();
      OkHttpClient client = new OkHttpClient();
      Request request = new Request.Builder()
          .url(url)
          .build();

      try (Response resp = client.newCall(request).execute()) {
        assert resp.body() != null;
        // System.err.println(resp.headers());
        contentType = resp.header("content-type");
        if (StringUtil.isBlank(contentType)) {
          contentType = resp.header("Content-Type");
        }
        while (!resp.body().source().exhausted()) {
          // response.content().writeByte(resp.body().source().readByte());
          response.content().writeBytes(resp.body().byteStream().readAllBytes());

          // System.err.println(resp.body().byteStream().readAllBytes().length);
        }
        // InputStream is = resp.body().byteStream();
        // response.content().writeBytes(is, is.available());
      } catch (IOException e) {
        e.printStackTrace();
        // throw e;
      }
    }
    response.headers().set(HttpHeaderNames.CONTENT_TYPE, contentType);
    response.headers().set(Constants.HEADER_REQUEST_ID, reqId);
    if (route.getCors() == 1) {
      response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
      response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS, "*");
      response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS, "*");
    }
    ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    return true;
  }

  public static boolean assertUserPass(ChannelHandlerContext ctx, Route route, User user, String reqId) {

    String[] aRoles = route.getAuthorizedRoles();
    String[] fRoles = route.getForbiddenRoles();
    if (DataUtil.isArrayEmpty(aRoles) && DataUtil.isArrayEmpty(fRoles)) {
      return true;
    }
    String[] myRoles = user.getRoles();
    if (myRoles == null) {
      return false;
    }
    if (fRoles != null) {
      for (String r : fRoles) {
        if (Arrays.asList(myRoles).contains(r)) {
          return false;
        }
      }
    }

    boolean passed = false;
    if (aRoles != null) {
      for (String r : aRoles) {
        if (Arrays.asList(myRoles).contains(r)) {
          passed = true;
          break;
        }
      }
    }
    if (!passed) {
      ResponseUtil.wrap(ctx, HttpResponseStatus.FORBIDDEN,
          Map.of(Constants.HEADER_REQUEST_ID, reqId), "Not Authorized.");

    }
    return passed;
  }

}
