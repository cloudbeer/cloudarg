package arche.cloud.netty;

import arche.cloud.netty.model.DataKeys;
import arche.cloud.netty.model.Route;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;

public class UserHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        System.out.println("user handler");
        Route route = ctx.channel().attr(DataKeys.ROUTE_INFO).get();
        System.out.println(route);
    }
}
