package arche.cloud.netty.client;

import arche.cloud.netty.exceptions.Responsable;
import arche.cloud.netty.model.DataKeys;
import arche.cloud.netty.model.Route;
import arche.cloud.netty.model.UserRequest;
import arche.cloud.netty.utils.ResponseUtil;
import arche.cloud.netty.utils.StringUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;

import java.util.HashMap;
import java.util.Locale;

public class RpcInboundHandler extends SimpleChannelInboundHandler<HttpObject> {


  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    cause.printStackTrace();
    ctx.close();
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) {

//    ChannelHandlerContext parentCtx = null;
//    Route apiInfo = null;
    ChannelHandlerContext parentCtx = ctx.channel().attr(DataKeys.PARENT_CONTEXT).get();
    Route apiInfo = ctx.channel().attr(DataKeys.API_INFO).get();
    UserRequest uq = ctx.channel().attr(DataKeys.REQUEST_INFO).get();

    HashMap<String, String> headers = new HashMap<>();
    headers.put("tracing_id", uq.getRequestId());

    CharSequence contentType = "application/json";
    HttpResponseStatus status = HttpResponseStatus.OK;
    if (msg instanceof HttpResponse response) {
      status = response.status();
      if (!response.headers().isEmpty()) {
        for (CharSequence name : response.headers().names()) {
          if (name.toString().toLowerCase(Locale.ROOT).equals("content-type")) {
            contentType = response.headers().get(name);
            headers.put("content-type", contentType.toString());
          } else {
            headers.put(name.toString(), response.headers().get(name));

          }
        }
      }
    }

    if (msg instanceof HttpContent content) {
//      System.out.println("this is content");
      if (StringUtil.isTextContentType(contentType.toString())) {
        ByteBuf lastContent = content.content();
//        System.out.println(lastContent.toString(CharsetUtil.UTF_8));

        if (apiInfo.getCors() == 1) {
          headers.put("cors", "true");
        }
        if (apiInfo.getWrapper() == 1) {
          ResponseUtil.wrap(parentCtx, HttpResponseStatus.OK,
                  headers,
                  lastContent);
        } else {
          headers.put("content-type", contentType.toString());
          ResponseUtil.pass(parentCtx, status, headers, lastContent);
        }
      } else {
        //TODO
        ResponseUtil.wrap(parentCtx, status, headers, "非文本返回尚未实现。");
      }
    }
  }
}
