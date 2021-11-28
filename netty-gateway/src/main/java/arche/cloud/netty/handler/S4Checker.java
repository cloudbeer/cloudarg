package arche.cloud.netty.handler;

import arche.cloud.netty.client.RpcClient;
import arche.cloud.netty.model.DataKeys;
import arche.cloud.netty.model.Route;
import arche.cloud.netty.model.User;
import arche.cloud.netty.model.UserRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;

public class S4Checker extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
        req.retain();
        Route route = ctx.channel().attr(DataKeys.API_INFO).get();
        UserRequest uq = ctx.channel().attr(DataKeys.REQUEST_INFO).get();
        User user = ctx.channel().attr(DataKeys.USER_INFO).get();

        RpcClient rpcClient = new RpcClient();
        rpcClient.setRoute(route);
        rpcClient.setParentRequest(req);
        rpcClient.setUser(user);
        rpcClient.setUserRequest(uq);
        rpcClient.setParentContext(ctx);
        rpcClient.accessBackend();

    }
}
