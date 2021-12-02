package arche.cloud.netty.handler;

import arche.cloud.netty.client.RpcClient;
import arche.cloud.netty.exceptions.Responsable;
import arche.cloud.netty.model.DataKeys;
import arche.cloud.netty.model.Route;
import arche.cloud.netty.model.User;
import arche.cloud.netty.model.UserRequest;
import arche.cloud.netty.utils.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class S4Checker extends SimpleChannelInboundHandler<FullHttpRequest> {
    Logger logger = LoggerFactory.getLogger(S2LoadRoute.class);

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("pipeline error", cause);
        ctx.close();
        // ResponseUtil.wrap(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR, headers,
        // cause.getMessage());
        // Uncaught exceptions from inbound handlers will propagate up to this handler
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) {
        req.retain();
        Route route = ctx.channel().attr(DataKeys.API_INFO).get();
        UserRequest uq = ctx.channel().attr(DataKeys.REQUEST_INFO).get();
        User user = ctx.channel().attr(DataKeys.USER_INFO).get();

        if (!DataUtil.isArrayEmpty(route.getAuthorizedRoles()) || !DataUtil.isArrayEmpty(route.getForbiddenRoles())) {

            try {
                RBACUtil.pass(
                        user.getRoles(),
                        route.getAuthorizedRoles(),
                        route.getForbiddenRoles());
            } catch (Responsable e) {
                e.printStackTrace();
                e.echo(ctx, uq.getRequestId(), uq.logInfo(), false);
                return;
            }

        }

        RpcClient rpcClient = new RpcClient();
        rpcClient.setRoute(route);
        rpcClient.setParentRequest(req);
        rpcClient.setUser(user);
        rpcClient.setUserRequest(uq);
        rpcClient.setParentContext(ctx);
        rpcClient.accessBackend();

    }
}
