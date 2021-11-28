package arche.cloud.netty.handler;

import arche.cloud.netty.model.DataKeys;
import arche.cloud.netty.model.Route;
import arche.cloud.netty.model.UserRequest;
import arche.cloud.netty.utils.DataUtil;
import arche.cloud.netty.utils.ResponseUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class S2LoadRoute extends SimpleChannelInboundHandler<FullHttpRequest> {
    Logger logger = LoggerFactory.getLogger(S2LoadRoute.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
        req.retain();
        UserRequest uq = ctx.channel().attr(DataKeys.REQUEST_INFO).get();
        Route route = DataUtil.getRouteInfo(uq.getPath()); // RequestUtil.getApiInfo(uq.getPath(), uq.getMethod());

        if (route == null) {
            logger.error("{} - {}", uq.logInfo(), "route not found.");
            ResponseUtil.echo(ctx, HttpResponseStatus.NOT_FOUND, uq.getRequestId(),"route not found: " + uq.getPath());
            return;
        }

        ctx.channel().attr(DataKeys.API_INFO).set(route);
        ctx.fireChannelRead(req);
//        ctx.pipeline().remove(this);


    }
}