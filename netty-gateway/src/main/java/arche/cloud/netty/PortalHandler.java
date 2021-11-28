package arche.cloud.netty;

import arche.cloud.netty.client.RpcClient;
import arche.cloud.netty.model.Route;
import arche.cloud.netty.model.UserRequest;
import arche.cloud.netty.utils.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;


/*
curl -v -X POST -F 'ticket1=linuxize' -F 'email=linuxize@example.com' \
    "localhost:8080/api/123?__ticket=some"

curl -X POST -d 'ticket=linuxize' -d 'email=linuxize@example.com' \
    "localhost:8080/api/123?__ticket=some"

curl -X POST -d 'ticket=linuxize&email=linuxize@example.com' \
    "localhost:8080/api/123?__ticket=some"

curl -X POST -H "Content-Type: application/json" \
    -d '{"ticket": "linuxize", "email": "linuxize@example.com"}' \
    "localhost:8080/api/123?__ticket=some"

curl "localhost:8080/api/123?__ticket=abcdeddd"

curl -X POST -F 'ticket=@/Users/cloudbeer/Downloads/c_alixhe.png' \
    "localhost:8080/api/123?__ticket=some&xxxx=ddd"

 */

public class PortalHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
        req.content().retain();
//        UserRequest uq = RequestUtil.parse(req);
//        Route route = DataUtil.getRouteInfo(uq.getPath()); // RequestUtil.getApiInfo(uq.getPath(), uq.getMethod());
//
//        if (route == null) {
//            ResponseUtil.echo(ctx, HttpResponseStatus.NOT_FOUND, "Api not found.");
//            return;
//        }
//        if (route.getAuthorizedRoles() != null || route.getForbiddenRoles() != null) {
//            final String ticket = uq.getTicket();
//            RpcClient.accessUser(ticket, ctx, route);
//
//            RpcClient rpcClient = new RpcClient();
//            rpcClient.setRoute(route);
//            rpcClient.setParentContext(ctx);
//            rpcClient.setParentRequest(req);
//            rpcClient.accessUser(ticket);

        }



//        ctx.channel().attr(DataKeys.ROUTE_INFO).set(route);
//        ctx.writeAndFlush(req);
//        ctx.fireChannelActive();

//        ChannelFuture future = ctx.newSucceededFuture();
//        future.addListener(ChannelFutureListener.);


//        ResponseUtil.echo(ctx, HttpResponseStatus.NOT_FOUND, "Api not found.");


//        User user = null;
//
//        if (apiInfo.getAuthorizedRoles() != null || apiInfo.getForbiddenRoles() != null) {
//            final String ticket = uq.getTicket();
//            if (ticket == null) {
//                ResponseUtil.echo(ctx, HttpResponseStatus.UNAUTHORIZED, "Ticket is required.");
//                return;
//            }
//
//            user = RequestUtil.getUser(ticket);
//            boolean canPass = RBACUtil.pass(user.getRoles(), apiInfo.getAuthorizedRoles(), apiInfo.getForbiddenRoles());
//
//            if (!canPass) {
//                ResponseUtil.echo(ctx, HttpResponseStatus.UNAUTHORIZED, "Not Authorized.");
//                return;
//            }
//        }

//        String backend = apiInfo.getBackendHost();
//        URI uri = URI.create(backend);
//        int port = uri.getPort();
//        String host = uri.getHost();
//        String schema = uri.getScheme();
//        if (StringUtil.isBlank(schema)) {
//            schema = "http";
//        }
//        if (port < 0) {
//            port = schema.equals("https") ? 443 : 80;
//        }


//        RpcClient rpcClient = new RpcClient();
////        rpcClient.setHost(host);
////        rpcClient.setPort(port);
//        rpcClient.setParentRequest(req);
//        rpcClient.setParentContext(ctx);
//        rpcClient.setUserRequest(uq);
//        rpcClient.setUser(user);
//        rpcClient.setRoute(apiInfo);
////        rpcClient.setApiInfo(apiInfo);
//        rpcClient.accessBackend();
//    }
}