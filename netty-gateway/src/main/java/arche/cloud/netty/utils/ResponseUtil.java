package arche.cloud.netty.utils;

import com.google.gson.Gson;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.util.HashMap;
import java.util.Map;

public class ResponseUtil {

    public static void echo(ChannelHandlerContext ctx, HttpResponseStatus status, Object data) {
        Map<String, Object> map = new HashMap<>();
        if (status == HttpResponseStatus.OK) {
            map.put("success", true);
        } else {
            map.put("success", false);
        }
        map.put("status", status.code());
        map.put("data", data);
        Gson gson = new Gson();
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.copiedBuffer(gson.toJson(map), CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=UTF-8");
        response.headers().set( "server", "cloudarg");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

}
