package arche.cloud.netty.handler;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import arche.cloud.netty.model.DataKeys;
import arche.cloud.netty.model.UserRequest;
import arche.cloud.netty.utils.RequestUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;

public class S1ParseRequest extends SimpleChannelInboundHandler<FullHttpRequest> {

    Logger logger = LoggerFactory.getLogger(S1ParseRequest.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) {

        String url = req.uri();
        // logger.info(url);
        if ("/metrics".equals(url)) {
            CommonHandler.echoMetrics(ctx, req);
            return;
        } else if ("/favicon.ico".equals(url)) {
            CommonHandler.echoFavicon(ctx, req);
            return;
        }
        req.retain();
        UserRequest uq = RequestUtil.parse(req);

        uq.setRequestId(UUID.randomUUID().toString());
        ctx.channel().attr(DataKeys.REQUEST_INFO).set(uq);
        ctx.fireChannelRead(req);
        // ctx.pipeline().remove(this);
    }
}
