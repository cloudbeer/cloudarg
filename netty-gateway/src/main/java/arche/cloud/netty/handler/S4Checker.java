package arche.cloud.netty.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import arche.cloud.netty.client.RpcClient;
import arche.cloud.netty.exceptions.Responsable;
import arche.cloud.netty.model.DataKeys;
import arche.cloud.netty.model.Route;
import arche.cloud.netty.model.User;
import arche.cloud.netty.model.UserRequest;
import arche.cloud.netty.utils.DataUtil;
import arche.cloud.netty.utils.RBACUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;

public class S4Checker extends SimpleChannelInboundHandler<FullHttpRequest> {
    Logger logger = LoggerFactory.getLogger(S4Checker.class);

    // @Override
    // public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

    // logger.error("pipeline error", cause);
    // if (cause instanceof ReadTimeoutException) {
    // UserRequest uq = ctx.channel().attr(DataKeys.REQUEST_INFO).get();
    // ResponseUtil.wrap(ctx, HttpResponseStatus.REQUEST_TIMEOUT,
    // Map.of(Constants.HEADER_REQUEST_ID, uq.getRequestId()), "Request timeout.");
    // }
    // ctx.close();
    // }

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
                // e.printStackTrace();
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
