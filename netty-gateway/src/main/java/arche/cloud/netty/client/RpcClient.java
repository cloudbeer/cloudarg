package arche.cloud.netty.client;

import arche.cloud.netty.model.ApiInfo;
import arche.cloud.netty.model.User;
import arche.cloud.netty.model.UserRequest;
import arche.cloud.netty.utils.RequestUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;

public class RpcClient {
    private String host;
    private int port;
    private ChannelHandlerContext parentContext;
    private FullHttpRequest parentRequest;
    private UserRequest userRequest;
    private ApiInfo apiInfo;
    private User user;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

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

    public ApiInfo getApiInfo() {
        return apiInfo;
    }

    public void setApiInfo(ApiInfo apiInfo) {
        this.apiInfo = apiInfo;
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
    }

    public void access() {


//        NioEventLoopGroup workGroup = null;
//        if (ColdBootstrap.BOOTSTRAP.group() == null) {
//            workGroup = new NioEventLoopGroup(10);
//            ColdBootstrap.BOOTSTRAP.group(workGroup);
//        }

//        AttributeKey<ChannelHandlerContext> PREV_CONTEXT = null;
//        if (AttributeKey.exists("parentContext")) {
//            PREV_CONTEXT = AttributeKey.valueOf("parentContext");
//        } else {
//            PREV_CONTEXT = AttributeKey.newInstance("parentContext");
//        }
//
//        AttributeKey<ApiInfo> API_INFO = null;
//        if (AttributeKey.exists("apiInfo")) {
//            API_INFO = AttributeKey.valueOf("apiInfo");
//        } else {
//            API_INFO = AttributeKey.newInstance("apiInfo");
//        }

//        System.out.println("----------");
//        System.out.println(parentRequest.content().toString(CharsetUtil.UTF_8));


        ColdChannelPool.BOOTSTRAP.remoteAddress(host, port);

        final FixedChannelPool pool = ColdChannelPool.POOLMAP.get(host);
        Future<Channel> future = pool.acquire();
        future.addListener((FutureListener<Channel>) channelFuture -> {
            if (future.isSuccess()) {
                Channel channel = future.getNow();

                channel.attr(ColdChannelPool.PARENT_CONTEXT).set(parentContext);
                channel.attr(ColdChannelPool.API_INFO).set(apiInfo);

                String query = userRequest.getQuery();
                String rpcUrl = apiInfo.getBackendPath();
                if (query != null) {
                    rpcUrl += "?" + userRequest.getQuery();
                }
//                System.out.println(parentRequest.content().toString(CharsetUtil.UTF_8));
                FullHttpRequest request = RequestUtil.copyRequest(parentRequest, rpcUrl);

////            HttpRequest request = new DefaultFullHttpRequest(
////                    HttpVersion.HTTP_1_1, method, rpcUrl, Unpooled.EMPTY_BUFFER);
                request.headers().set(HttpHeaderNames.HOST, host + ":" + port);
                request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                request.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);
//
//                System.out.println(request.content());
//            channel.writeAndFlush(request);
//            channel.closeFuture().sync();

                channel.write(request);
                channel.writeAndFlush(Unpooled.EMPTY_BUFFER);
//                System.out.println(channel.id());
                pool.release(channel);
            }
        });


//        try {
//            ColdBootstrap.BOOTSTRAP.attr(PREV_CONTEXT, parentContext);
//            ColdBootstrap.BOOTSTRAP.attr(API_INFO, apiInfo);
//
//            // Start the client.
//            ChannelFuture f = ColdBootstrap.BOOTSTRAP.connect(host, port).sync();
//
//            String query = userRequest.getQuery();
//            String rpcUrl = apiInfo.getBackendPath();
//            if (query != null) {
//                rpcUrl += "?" + userRequest.getQuery();
//            }
//            HttpRequest request = RequestUtil.copyRequest(parentRequest, rpcUrl);
////            HttpRequest request = new DefaultFullHttpRequest(
////                    HttpVersion.HTTP_1_1, method, rpcUrl, Unpooled.EMPTY_BUFFER);
//            request.headers().set(HttpHeaderNames.HOST, host + ":" + port);
//            request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
//            request.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);
//
//            f.channel().writeAndFlush(request);
//            f.channel().closeFuture().sync();
//
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } finally {
//            if (workGroup != null) {
//                workGroup.shutdownGracefully();
//            }
//        }
    }
}
