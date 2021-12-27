package arche.cloud.netty.handler;

import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import arche.cloud.netty.model.Constants;
import arche.cloud.netty.model.DataKeys;
import arche.cloud.netty.model.Route;
import arche.cloud.netty.model.UserRequest;
import arche.cloud.netty.utils.RequestUtil;
import arche.cloud.netty.utils.ResponseUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.timeout.ReadTimeoutException;

public class S1ParseRequest extends SimpleChannelInboundHandler<FullHttpRequest> {

    Logger logger = LoggerFactory.getLogger(S1ParseRequest.class);

    private String reqId = null;

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

        // logger.info(reqId);
        logger.error("pipeline timeout", cause);
        if (cause instanceof ReadTimeoutException) {
            if (reqId != null) {
                Route route = ctx.channel().attr(DataKeys.API_INFO).get();
                if (route != null) {
                    System.err.println(route);
                    // logger.error("backend timeout", route.toString());
                    ResponseUtil.wrap(ctx, HttpResponseStatus.GATEWAY_TIMEOUT,
                            Map.of(Constants.HEADER_REQUEST_ID, reqId), "Gateway Timeout.");
                }
            }
        }
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) {

        String url = req.uri();
        // logger.info(url);
        if ("/metrics".equals(url)) {
            CommonHandler.echoMetrics(ctx, req);
            ctx.close();
            return;
        } else if ("/favicon.ico".equals(url)) {
            CommonHandler.echoFavicon(ctx, req);
            ctx.close();
            return;
        }
        req.retain();
        reqId = UUID.randomUUID().toString();
        UserRequest uq = RequestUtil.parse(req);

        uq.setRequestId(reqId);
        ctx.channel().attr(DataKeys.REQUEST_INFO).set(uq);
        ctx.fireChannelRead(req);
        // ctx.pipeline().remove(this);
    }
}
