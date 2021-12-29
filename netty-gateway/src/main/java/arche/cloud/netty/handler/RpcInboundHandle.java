package arche.cloud.netty.handler;

import java.util.HashMap;
import java.util.Locale;

import arche.cloud.netty.model.Constants;
import arche.cloud.netty.model.Route;
import arche.cloud.netty.model.UserRequest;
import arche.cloud.netty.utils.ResponseUtil;
import arche.cloud.netty.utils.StringUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

public class RpcInboundHandle extends ChannelInboundHandlerAdapter {
  private final Channel inboundChannel;
  private final Route route;
  private final UserRequest uq;

  public RpcInboundHandle(Channel inboundChannel, Route route, UserRequest uq) {
    this.inboundChannel = inboundChannel;
    this.route = route;
    this.uq = uq;
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) {
    // System.out.println("Hello");
    ctx.read();
  }

  @Override
  public void channelRead(final ChannelHandlerContext ctx, Object msg) {
    // System.out.println("Read");

    HashMap<String, String> headers = new HashMap<>();
    headers.put(Constants.HEADER_REQUEST_ID, uq.getRequestId());

    CharSequence contentType = "application/json";
    HttpResponseStatus status = HttpResponseStatus.OK;

    // System.out.println("\n\n\n------------------" + uq.getQuery() +
    // "-------------");
    // System.out.println(status);

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
      if (StringUtil.isTextContentType(contentType.toString())) {
        ByteBuf lastContent = content.content();

        // System.out.println(lastContent.toString(CharsetUtil.UTF_8));

        if (route.getCors() == 1) {
          headers.put("cors", "true");
        }
        if (route.getWrapper() == 1) {
          ResponseUtil.wrap(inboundChannel, HttpResponseStatus.OK,
              headers,
              lastContent);
        } else {
          headers.put("content-type", contentType.toString());
          ResponseUtil.pass(inboundChannel, status, headers, lastContent);
        }
        lastContent.release();
      } else {
        // TODO 非文本返回尚未实现。
        ResponseUtil.wrap(inboundChannel, status, headers, "非文本返回尚未实现。");
      }
    }
    ctx.close();
    // inboundChannel.closeFuture().syncUninterruptibly();
  }
}
