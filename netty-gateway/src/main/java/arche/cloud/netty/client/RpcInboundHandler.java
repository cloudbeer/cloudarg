package arche.cloud.netty.client;

import arche.cloud.netty.model.ApiInfo;
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
    ApiInfo apiInfo = null;



    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
//        if (AttributeKey.exists("parentContext") && parentCtx == null) {
//            AttributeKey<ChannelHandlerContext> PREV_CONTEXT = AttributeKey.valueOf("parentContext");
//            Attribute<ChannelHandlerContext> xx = ctx.channel().attr(PREV_CONTEXT);
//            parentCtx = xx.get();
//        }
//        if (AttributeKey.exists("apiInfo") && apiInfo == null) {
//            AttributeKey<ApiInfo> API_INFO = AttributeKey.valueOf("apiInfo");
//            Attribute<ApiInfo> xx = ctx.channel().attr(API_INFO);
//            apiInfo = xx.get();
//        }

        parentCtx = ctx.channel().attr(ColdChannelPool.PARENT_CONTEXT).get();
        apiInfo = ctx.channel().attr(ColdChannelPool.API_INFO).get();

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
                if (apiInfo.isWrapperResponse()) {
                    ResponseUtil.echo(parentCtx, HttpResponseStatus.OK, lastContent.toString(CharsetUtil.UTF_8));
                } else {
//                    System.out.println(status + " " + contentType);
                    ResponseUtil.echo(parentCtx, status, contentType, lastContent);
                }
            }
        }
    }
}
