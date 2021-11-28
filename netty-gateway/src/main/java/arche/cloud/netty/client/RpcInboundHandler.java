package arche.cloud.netty.client;

import arche.cloud.netty.model.DataKeys;
import arche.cloud.netty.model.Route;
import arche.cloud.netty.model.UserRequest;
import arche.cloud.netty.utils.ResponseUtil;
import arche.cloud.netty.utils.StringUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;

import java.util.Locale;

public class RpcInboundHandler extends SimpleChannelInboundHandler<HttpObject> {

    ChannelHandlerContext parentCtx = null;
    Route apiInfo = null;

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) {

        parentCtx = ctx.channel().attr(DataKeys.PARENT_CONTEXT).get();
        apiInfo = ctx.channel().attr(DataKeys.API_INFO).get();
        UserRequest uq = ctx.channel().attr(DataKeys.REQUEST_INFO).get();

        CharSequence contentType = "application/json";
        HttpResponseStatus status = HttpResponseStatus.OK;
        if (msg instanceof HttpResponse response) {
            status = response.status();
            if (!response.headers().isEmpty()) {
                for (CharSequence name : response.headers().names()) {
                    if (name.toString().toLowerCase(Locale.ROOT).equals("content-type")) {
                        contentType = response.headers().get(name);
                        break;
                    }
                }
            }
        }

        if (msg instanceof HttpContent content) {
            if (StringUtil.isTextContentType(contentType.toString())) {
                ByteBuf lastContent = content.content();
                if (apiInfo.getWrapper() == 1) {
                    ResponseUtil.echo(parentCtx, HttpResponseStatus.OK,
                            uq.getRequestId(),
                            lastContent.toString(CharsetUtil.UTF_8));
                } else {
                    ResponseUtil.echo(parentCtx, status, contentType, lastContent);
                }
            }
        }
    }
}
