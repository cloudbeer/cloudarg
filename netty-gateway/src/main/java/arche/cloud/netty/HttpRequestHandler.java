package arche.cloud.netty;

import arche.cloud.netty.model.HttpApi;
import arche.cloud.netty.model.User;
import arche.cloud.netty.model.UserRequest;
import arche.cloud.netty.utils.RBACUtil;
import arche.cloud.netty.utils.RequestUtil;
import arche.cloud.netty.utils.ResponseUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;


/*
curl -v -X POST -F 'ticket1=linuxize' -F 'email=linuxize@example.com' \
    "localhost:8080/api/123"

curl -X POST -d 'ticket=linuxize' -d 'email=linuxize@example.com' \
    "localhost:8080/api/123"

curl -X POST -d 'ticket=linuxize&email=linuxize@example.com' \
    "localhost:8080/api/123"

curl -X POST -H "Content-Type: application/json" \
    -d '{"ticket": "linuxize", "email": "linuxize@example.com"}' \
    "localhost:8080/api/123"

curl "localhost:8080/api/123?ticket=abcdeddd"

curl -X POST -F 'ticket=@/Users/cloudbeer/Downloads/c_alixhe.png' \
    "localhost:8080/api/123"

 */

public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, FullHttpRequest req) {
        UserRequest uq = RequestUtil.parse(req);

        HttpApi apiInfo = RequestUtil.getApiInfo(uq.getPath(), uq.getMethod());

        if (apiInfo == null) {
            ResponseUtil.echo(ctx, HttpResponseStatus.NOT_FOUND, "Api not found.");
            return;
        }

        if (apiInfo.getAuthorizedRoles() != null || apiInfo.getForbiddenRoles() != null) {
            final String ticket = uq.getTicket();
            if (ticket == null) {
                ResponseUtil.echo(ctx, HttpResponseStatus.UNAUTHORIZED, "Ticket is required.");
                return;
            }
            User user = RequestUtil.getUser(ticket);
            boolean canPass = RBACUtil.pass(user.getRoles(), apiInfo.getAuthorizedRoles(), apiInfo.getForbiddenRoles());

            if (!canPass) {
                ResponseUtil.echo(ctx, HttpResponseStatus.UNAUTHORIZED, "Not Authorized.");
                return;
            }
        }


        ResponseUtil.echo(ctx, HttpResponseStatus.OK, "direct pass");
    }
}