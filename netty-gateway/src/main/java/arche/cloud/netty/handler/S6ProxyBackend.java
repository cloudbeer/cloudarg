// package arche.cloud.netty.handler;

// import java.util.HashMap;
// import java.util.Locale;

// import arche.cloud.netty.model.Constants;
// import arche.cloud.netty.model.DataKeys;
// import arche.cloud.netty.model.Route;
// import arche.cloud.netty.model.UserRequest;
// import arche.cloud.netty.utils.ResponseUtil;
// import arche.cloud.netty.utils.StringUtil;
// import io.netty.buffer.ByteBuf;
// import io.netty.channel.Channel;
// import io.netty.channel.ChannelHandlerContext;
// import io.netty.channel.SimpleChannelInboundHandler;
// import io.netty.handler.codec.http.HttpContent;
// import io.netty.handler.codec.http.HttpObject;
// import io.netty.handler.codec.http.HttpResponse;
// import io.netty.handler.codec.http.HttpResponseStatus;

// public class S6ProxyBackend extends SimpleChannelInboundHandler<HttpObject> {

//   private final Channel inboundChannel;

//   public S6ProxyBackend(Channel inboundChannel) {
//     this.inboundChannel = inboundChannel;
//   }

//   @Override
//   public void channelActive(ChannelHandlerContext ctx) {
//     System.out.println("in S6");
//     ctx.read();
//   }

//   @Override
//   protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) {

//     // ChannelHandlerContext parentCtx = null;
//     // Route apiInfo = null;
//     // ChannelHandlerContext parentCtx =
//     // ctx.channel().attr(DataKeys.PARENT_CONTEXT).get();
//     Route apiInfo = inboundChannel.attr(DataKeys.API_INFO).get();
//     UserRequest uq = inboundChannel.attr(DataKeys.REQUEST_INFO).get();

//     HashMap<String, String> headers = new HashMap<>();
//     headers.put(Constants.HEADER_REQUEST_ID, uq.getRequestId());

//     CharSequence contentType = "application/json";
//     HttpResponseStatus status = HttpResponseStatus.OK;

//     // System.out.println("\n\n\n------------------" + uq.getQuery() +
//     // "-------------");
//     // System.out.println(status);

//     if (msg instanceof HttpResponse response) {
//       status = response.status();
//       if (!response.headers().isEmpty()) {
//         for (CharSequence name : response.headers().names()) {
//           if (name.toString().toLowerCase(Locale.ROOT).equals("content-type")) {
//             contentType = response.headers().get(name);
//             headers.put("content-type", contentType.toString());
//           } else {
//             headers.put(name.toString(), response.headers().get(name));

//           }
//         }
//       }
//     }

//     if (msg instanceof HttpContent content) {
//       if (StringUtil.isTextContentType(contentType.toString())) {
//         ByteBuf lastContent = content.content();

//         // System.out.println(lastContent.toString(CharsetUtil.UTF_8));

//         if (apiInfo.getCors() == 1) {
//           headers.put("cors", "true");
//         }
//         if (apiInfo.getWrapper() == 1) {
//           ResponseUtil.wrap(inboundChannel, HttpResponseStatus.OK,
//               headers,
//               lastContent);
//         } else {
//           headers.put("content-type", contentType.toString());
//           ResponseUtil.pass(inboundChannel, status, headers, lastContent);
//         }
//       } else {
//         // TODO 非文本返回尚未实现。
//         ResponseUtil.wrap(ctx, status, headers, "非文本返回尚未实现。");
//       }
//     }

//   }

//   @Override
//   public void channelInactive(ChannelHandlerContext ctx) {
//     S5Proxy.closeOnFlush(inboundChannel);
//   }

//   @Override
//   public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
//     cause.printStackTrace();
//     S5Proxy.closeOnFlush(ctx.channel());
//   }

// }
