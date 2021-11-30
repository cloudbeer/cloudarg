package arche.cloud.netty.handler;

import arche.cloud.netty.exceptions.Responsable;
import arche.cloud.netty.model.DataKeys;
import arche.cloud.netty.model.Route;
import arche.cloud.netty.model.User;
import arche.cloud.netty.model.UserRequest;
import arche.cloud.netty.utils.DataUtil;
import arche.cloud.netty.utils.ResponseUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class S3LoadUser extends SimpleChannelInboundHandler<FullHttpRequest> {
    Logger logger = LoggerFactory.getLogger(S2LoadRoute.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) {
        req.retain();
        Route route = ctx.channel().attr(DataKeys.API_INFO).get();
        UserRequest uq = ctx.channel().attr(DataKeys.REQUEST_INFO).get();
        String[] aRoles = route.getAuthorizedRoles();
        String[] fRoles =route.getForbiddenRoles();
        if (DataUtil.isArrayEmpty(aRoles) && DataUtil.isArrayEmpty(fRoles)){
            ctx.fireChannelRead(req);
            return;
        }

        try {
            User user = DataUtil.getUser(uq.getTicket());
            ctx.channel().attr(DataKeys.USER_INFO).set(user);
            ctx.fireChannelRead(req);
//            ResponseUtil.echo(ctx, HttpResponseStatus.OK, user);
        } catch (Responsable e) {
//            logger.error("error", e);
            e.echo(ctx, uq.getRequestId(), uq.logInfo(), false);

//            ResponseUtil.echo(ctx, HttpResponseStatus.UNAUTHORIZED, uq.getRequestId(), e.getMessage());
        }
    }
}
