package arche.cloud.netty.handler;

import arche.cloud.netty.client.RpcClient;
import arche.cloud.netty.model.DataKeys;
import arche.cloud.netty.model.Route;
import arche.cloud.netty.model.User;
import arche.cloud.netty.model.UserRequest;
import arche.cloud.netty.utils.RBACUtil;
import arche.cloud.netty.utils.RequestUtil;
import arche.cloud.netty.utils.ResponseUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class S4Checker extends SimpleChannelInboundHandler<FullHttpRequest> {
    Logger logger = LoggerFactory.getLogger(S2LoadRoute.class);


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) {
        req.retain();
        Route route = ctx.channel().attr(DataKeys.API_INFO).get();
        UserRequest uq = ctx.channel().attr(DataKeys.REQUEST_INFO).get();
        User user = ctx.channel().attr(DataKeys.USER_INFO).get();


        if (route.getAuthorizedRoles() != null || route.getForbiddenRoles() != null) {

            boolean canPass = RBACUtil.pass(
                    user.getRoles(),
                    route.getAuthorizedRoles(),
                    route.getForbiddenRoles());

            if (!canPass) {
                logger.error( uq.logInfo());
                ResponseUtil.echo(ctx, HttpResponseStatus.UNAUTHORIZED, uq.getRequestId(), "Not Authorized.");

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
