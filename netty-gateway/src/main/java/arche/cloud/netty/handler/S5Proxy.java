// package arche.cloud.netty.handler;

// import arche.cloud.netty.model.Backend;
// import arche.cloud.netty.model.Route;
// import arche.cloud.netty.model.User;
// import arche.cloud.netty.model.UserRequest;
// import arche.cloud.netty.utils.RequestUtil;
// import io.netty.bootstrap.Bootstrap;
// import io.netty.buffer.Unpooled;
// import io.netty.channel.Channel;
// import io.netty.channel.ChannelFuture;
// import io.netty.channel.ChannelFutureListener;
// import io.netty.channel.ChannelHandlerContext;
// import io.netty.channel.ChannelInboundHandlerAdapter;
// import io.netty.channel.SimpleChannelInboundHandler;
// import io.netty.handler.codec.http.FullHttpRequest;
// import io.netty.handler.codec.http.HttpHeaderNames;
// import io.netty.handler.codec.http.HttpHeaderValues;

// public class S5Proxy extends ChannelInboundHandlerAdapter {

// private UserRequest userRequest;
// private Route route;
// private User user;
// private Backend backend;

// public UserRequest getUserRequest() {
// return userRequest;
// }

// public void setUserRequest(UserRequest userRequest) {
// this.userRequest = userRequest;
// }

// public Route getRoute() {
// return route;
// }

// public void setRoute(Route route) {
// this.route = route;
// }

// public User getUser() {
// return user;
// }

// public void setUser(User user) {
// this.user = user;
// }

// public Backend getBackend() {
// return backend;
// }

// public void setBackend(Backend backend) {
// this.backend = backend;
// }

// private Channel outboundChannel;

// @Override
// public void channelActive(ChannelHandlerContext ctx) {

// System.out.println("s5 active.");

// final Channel inboundChannel = ctx.channel();

// // Start the connection attempt.
// Bootstrap b = new Bootstrap();
// b.group(inboundChannel.eventLoop())
// .channel(ctx.channel().getClass())
// .handler(new S6ProxyBackend(inboundChannel));

// ChannelFuture f = b.connect(backend.getHost(), backend.getPort());
// outboundChannel = f.channel();
// f.addListener(new ChannelFutureListener() {
// @Override
// public void operationComplete(ChannelFuture future) {
// if (future.isSuccess()) {
// // connection complete start to read first data
// inboundChannel.read();
// } else {
// // Close the connection if the connection attempt has failed.
// inboundChannel.close();
// }
// }
// });
// }

// @Override
// public void channelRead(final ChannelHandlerContext ctx, Object req) {

// System.out.println("read s5 ---- " + req.getClass());

// if (outboundChannel.isActive()) {

// String query = userRequest.getQuery();
// String queryPath = userRequest.getPath();
// String routePath = route.getFullPath();
// String rpcUrl = backend.toUrl();
// if (routePath.endsWith("/")) {
// rpcUrl += queryPath.substring(routePath.length());
// }
// if (query != null) {
// rpcUrl += "?" + userRequest.getQuery();
// }

// FullHttpRequest request = RequestUtil.copyRequest((FullHttpRequest) req,
// rpcUrl);

// request.headers().set(HttpHeaderNames.HOST, backend.getHost() + ":" +
// backend.getPort());
// // request.headers().set(HttpHeaderNames.CONNECTION,
// // HttpHeaderValues.KEEP_ALIVE);
// request.headers().set(HttpHeaderNames.ACCEPT_ENCODING,
// HttpHeaderValues.GZIP);

// outboundChannel.writeAndFlush(request).addListener(new
// ChannelFutureListener() {
// @Override
// public void operationComplete(ChannelFuture future) {
// if (future.isSuccess()) {
// // was able to flush out data, start to read the next chunk
// ctx.channel().read();
// } else {
// future.channel().close();
// }
// }
// });
// }
// }

// @Override
// public void channelInactive(ChannelHandlerContext ctx) {
// if (outboundChannel != null) {
// closeOnFlush(outboundChannel);
// }
// }

// @Override
// public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
// cause.printStackTrace();
// closeOnFlush(ctx.channel());
// }

// /**
// * Closes the specified channel after all queued write requests are flushed.
// */
// static void closeOnFlush(Channel ch) {
// if (ch.isActive()) {
// ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
// }
// }

// }

// // package arche.cloud.netty.handler;

// // import arche.cloud.netty.model.Backend;
// // import arche.cloud.netty.model.Route;
// // import arche.cloud.netty.model.User;
// // import arche.cloud.netty.model.UserRequest;
// // import arche.cloud.netty.utils.RequestUtil;
// // import io.netty.bootstrap.Bootstrap;
// // import io.netty.buffer.Unpooled;
// // import io.netty.channel.Channel;
// // import io.netty.channel.ChannelFuture;
// // import io.netty.channel.ChannelFutureListener;
// // import io.netty.channel.ChannelHandlerContext;
// // import io.netty.channel.SimpleChannelInboundHandler;
// // import io.netty.handler.codec.http.FullHttpRequest;
// // import io.netty.handler.codec.http.HttpHeaderNames;
// // import io.netty.handler.codec.http.HttpHeaderValues;

// // public class S5Proxy extends SimpleChannelInboundHandler<FullHttpRequest>
// {

// // private UserRequest userRequest;
// // private Route route;
// // private User user;
// // private Backend backend;

// // public UserRequest getUserRequest() {
// // return userRequest;
// // }

// // public void setUserRequest(UserRequest userRequest) {
// // this.userRequest = userRequest;
// // }

// // public Route getRoute() {
// // return route;
// // }

// // public void setRoute(Route route) {
// // this.route = route;
// // }

// // public User getUser() {
// // return user;
// // }

// // public void setUser(User user) {
// // this.user = user;
// // }

// // public Backend getBackend() {
// // return backend;
// // }

// // public void setBackend(Backend backend) {
// // this.backend = backend;
// // }

// // private Channel outboundChannel;

// // @Override
// // public void channelActive(ChannelHandlerContext ctx) {

// // final Channel inboundChannel = ctx.channel();

// // // Start the connection attempt.
// // Bootstrap b = new Bootstrap();
// // b.group(inboundChannel.eventLoop())
// // .channel(ctx.channel().getClass())
// // .handler(new S6ProxyBackend(inboundChannel));

// // ChannelFuture f = b.connect(backend.getHost(), backend.getPort());
// // outboundChannel = f.channel();
// // f.addListener(new ChannelFutureListener() {
// // @Override
// // public void operationComplete(ChannelFuture future) {
// // if (future.isSuccess()) {
// // // connection complete start to read first data
// // inboundChannel.read();
// // } else {
// // // Close the connection if the connection attempt has failed.
// // inboundChannel.close();
// // }
// // }
// // });
// // }

// // @Override
// // public void channelRead0(final ChannelHandlerContext ctx, FullHttpRequest
// // req) {

// // System.out.println("active s5");

// // if (outboundChannel.isActive()) {

// // String query = userRequest.getQuery();
// // String queryPath = userRequest.getPath();
// // String routePath = route.getFullPath();
// // String rpcUrl = backend.toUrl();
// // if (routePath.endsWith("/")) {
// // rpcUrl += queryPath.substring(routePath.length());
// // }
// // if (query != null) {
// // rpcUrl += "?" + userRequest.getQuery();
// // }

// // FullHttpRequest request = RequestUtil.copyRequest(req, rpcUrl);

// // request.headers().set(HttpHeaderNames.HOST, backend.getHost() + ":" +
// // backend.getPort());
// // // request.headers().set(HttpHeaderNames.CONNECTION,
// // // HttpHeaderValues.KEEP_ALIVE);
// // request.headers().set(HttpHeaderNames.ACCEPT_ENCODING,
// // HttpHeaderValues.GZIP);

// // outboundChannel.writeAndFlush(request).addListener(new
// // ChannelFutureListener() {
// // @Override
// // public void operationComplete(ChannelFuture future) {
// // if (future.isSuccess()) {
// // // was able to flush out data, start to read the next chunk
// // ctx.channel().read();
// // } else {
// // future.channel().close();
// // }
// // }
// // });
// // }
// // }

// // @Override
// // public void channelInactive(ChannelHandlerContext ctx) {
// // if (outboundChannel != null) {
// // closeOnFlush(outboundChannel);
// // }
// // }

// // @Override
// // public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
// // cause.printStackTrace();
// // closeOnFlush(ctx.channel());
// // }

// // /**
// // * Closes the specified channel after all queued write requests are
// flushed.
// // */
// // static void closeOnFlush(Channel ch) {
// // if (ch.isActive()) {
// //
// ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
// // }
// // }

// // }
