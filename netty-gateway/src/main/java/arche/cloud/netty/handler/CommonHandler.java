package arche.cloud.netty.handler;

import java.io.IOException;
import java.io.InputStream;

import arche.cloud.netty.model.Route;
import arche.cloud.netty.utils.StringUtil;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
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

  public static void echoMetrics(ChannelHandlerContext ctx, FullHttpRequest req) {

    FullHttpResponse response = new DefaultFullHttpResponse(
        HttpVersion.HTTP_1_1,
        HttpResponseStatus.OK);
    response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
    response.content().writeCharSequence("metrics", CharsetUtil.UTF_8);

    ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    ctx.close();
  }

  public static void echoFavicon(ChannelHandlerContext ctx, FullHttpRequest req) {

    FullHttpResponse response = new DefaultFullHttpResponse(
        HttpVersion.HTTP_1_1,
        HttpResponseStatus.OK);
    response.headers().set(HttpHeaderNames.CONTENT_TYPE, "image/x-icon");
    InputStream is = CommonHandler.class.getClassLoader().getResourceAsStream("logo.ico");
    assert is != null;
    // InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
    try {
      response.content().writeBytes(is, is.available());
    } catch (IOException e) {
      e.printStackTrace();
    }
    ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    ctx.close();
  }

  public static void echoMockContent(ChannelHandlerContext ctx, FullHttpRequest req, Route route) {

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
        contentType = "Blank content.";
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
    ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    ctx.close();
  }

}
