package arche.cloud.netty.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import arche.cloud.netty.exceptions.Responsable;
import arche.cloud.netty.model.DataKeys;
import arche.cloud.netty.model.Route;
import arche.cloud.netty.model.User;
import arche.cloud.netty.model.UserRequest;
import arche.cloud.netty.utils.DataUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;

public class S3LoadUser extends SimpleChannelInboundHandler<FullHttpRequest> {
    Logger logger = LoggerFactory.getLogger(S2LoadRoute.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) {
        req.retain();
        Route route = ctx.channel().attr(DataKeys.API_INFO).get();
        UserRequest uq = ctx.channel().attr(DataKeys.REQUEST_INFO).get();
        String[] aRoles = route.getAuthorizedRoles();
        String[] fRoles = route.getForbiddenRoles();
        if (DataUtil.isArrayEmpty(aRoles) && DataUtil.isArrayEmpty(fRoles)) {
            ctx.fireChannelRead(req);
            return;
        }

        try {
            User user = DataUtil.getUser(uq.getTicket());
            ctx.channel().attr(DataKeys.USER_INFO).set(user);
            ctx.fireChannelRead(req);
            // ResponseUtil.echo(ctx, HttpResponseStatus.OK, user);
        } catch (Responsable e) {
            // logger.error("error", e);
            e.printStackTrace();
            e.echo(ctx, uq.getRequestId(), uq.logInfo(), false);
            req.release();

            // ResponseUtil.echo(ctx, HttpResponseStatus.UNAUTHORIZED, uq.getRequestId(),
            // e.getMessage());
        }
    }
}
