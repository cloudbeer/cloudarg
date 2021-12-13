package arche.cloud.netty.utils;

import java.io.Serializable;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

public class ResponseUtil {
  private ResponseUtil() {
    throw new IllegalStateException("Utility class");
  }

  private static void mixinHeaders(FullHttpResponse response, Map<String, String> headers) {
    response.headers().set("server", "cloudarg");
    headers.forEach((key, value) -> {
      if (!"cors".equals(key)) {
        response.headers().set(key, value);
      }
    });

    String cors = headers.get("cors");
    if ("true".equals(cors)) {
      response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
      response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS, "*");
      response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS, "*");
    }
  }

  public static void wrap(ChannelHandlerContext ctx,
      HttpResponseStatus status,
      Map<String, String> headers,
      Object data) {
    FullHttpResponse response = new DefaultFullHttpResponse(
        HttpVersion.HTTP_1_1,
        status);
    mixinHeaders(response, headers);
    String contentType = headers.get("content-type");
    if (contentType == null) {
      contentType = "text/plain";
    }
    if (status == HttpResponseStatus.OK) {
      wrapResponse(response, data, contentType.contains("json"));
    } else {
      Map<String, Object> result = new HashMap<>();
      result.put("service", "cloudarg");
      result.put("success", false);
      Map<String, Object> error = new HashMap<>();
      error.put("code", status.code());
      error.put("message", data);
      result.put("error", error);
      response.content().writeBytes(Unpooled.copiedBuffer(GsonUtil.serialize(result), CharsetUtil.UTF_8));
    }
    response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=UTF-8");
    ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    ctx.close();
  }

  private static void wrapResponse(FullHttpResponse response, Object data, boolean isJson) {
    String pre = "{\"service\":\"cloudarg\",\"success\":true,\"data\":";
    response.content().writeBytes(Unpooled.copiedBuffer(pre, CharsetUtil.UTF_8));
    if (data instanceof ByteBuf content) {
      // System.out.println(content.toString(CharsetUtil.UTF_8));
      if (isJson) {
        response.content().writeBytes(Unpooled.copiedBuffer(content));
      } else {
        response.content().writeBytes(Unpooled.copiedBuffer("\"", CharsetUtil.UTF_8));
        response.content().writeBytes(Base64.getEncoder().encode(content.nioBuffer()));
        response.content().writeBytes(Unpooled.copiedBuffer("\"", CharsetUtil.UTF_8));
      }
      // response.content().writeBytes(content);
    } else if (data instanceof String content) {
      response.content().writeBytes(Unpooled.copiedBuffer(content, CharsetUtil.UTF_8));
    } else if (data instanceof Serializable content) {
      response.content().writeBytes(Unpooled.copiedBuffer(GsonUtil.serialize(content), CharsetUtil.UTF_8));
    } else {
      response.setStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR);
      response.content().writeBytes(Unpooled.copiedBuffer("not support now.", CharsetUtil.UTF_8));
    }

    response.content().writeBytes(Unpooled.copiedBuffer("}", CharsetUtil.UTF_8));
    response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
  }

  public static void pass(ChannelHandlerContext ctx,
      HttpResponseStatus status,
      HashMap<String, String> headers,
      ByteBuf data) {
    FullHttpResponse response = new DefaultFullHttpResponse(
        HttpVersion.HTTP_1_1,
        status,
        Unpooled.copiedBuffer(data));
    mixinHeaders(response, headers);
    // response.headers().set(HttpHeaderNames.CONTENT_TYPE, contentType);
    response.headers().set("server", "cloudarg");
    ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    ctx.close();
  }

  // public static void echo(ChannelHandlerContext ctx,
  // HttpResponseStatus status,
  // String requestId,
  // Object data) {
  // Map<String, Object> map = new HashMap<>();
  // if (status == HttpResponseStatus.OK) {
  // map.put("success", true);
  // } else {
  // map.put("success", false);
  // }
  // map.put("request_id", requestId);
  // map.put("status", status.code());
  // map.put("data", data);
  // Gson gson = new Gson();
  // FullHttpResponse response = new DefaultFullHttpResponse(
  // HttpVersion.HTTP_1_1,
  // status,
  // Unpooled.copiedBuffer(gson.toJson(map), CharsetUtil.UTF_8));
  // response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json;
  // charset=UTF-8");
  // response.headers().set("server", "cloudarg");
  // ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
  //// ctx.close();
  // }
  //
  // public static void echo(ChannelHandlerContext ctx,
  // HttpResponseStatus status,
  // CharSequence contentType,
  // ByteBuf data) {
  // FullHttpResponse response = new DefaultFullHttpResponse(
  // HttpVersion.HTTP_1_1,
  // status,
  // Unpooled.copiedBuffer(data));
  // response.headers().set(HttpHeaderNames.CONTENT_TYPE, contentType);
  // response.headers().set("server", "cloudarg");
  // ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
  // ctx.close();
  // }

}
