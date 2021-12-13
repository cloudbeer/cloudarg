package arche.cloud.netty.handler;

import java.io.IOException;
import java.io.InputStream;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

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

}
