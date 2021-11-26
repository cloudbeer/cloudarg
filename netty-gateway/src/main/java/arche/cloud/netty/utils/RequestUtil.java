package arche.cloud.netty.utils;

import arche.cloud.netty.model.ApiInfo;
import arche.cloud.netty.model.User;
import arche.cloud.netty.model.UserRequest;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RequestUtil {
    /**
     * 从一个请求中获取必要的参数
     *
     * @param request HttpServletRequest 实例
     * @return 请求模型
     */
    public static UserRequest parse(FullHttpRequest request) {
        String url = StringUtil.toLowerCase(request.uri(), null);

        HttpHeaders headers = request.headers();
        String contentType = "application/json";
        if (!headers.isEmpty()) {
            for (CharSequence name : headers.names()) {
                if (name.toString().toLowerCase(Locale.ROOT).equals("content-type")) {
                    contentType = headers.get(name).toString();
                    break;
                }
            }
        }
//        ByteBuf content = request.content();
//        System.out.println(content.toString(CharsetUtil.UTF_8));
        String method = StringUtil.toUpperCase(request.method().name(), "GET");

//        ByteBuf content = null;

        // 约定 ticket 必须放在 url 的 query 的 __ticket 中，不能放在其他地方
        String ticket = getFromQuery(url, "__ticket");


//        if (!"GET".contentEquals(method)) {
//            content = request.content();
//        }

        UserRequest rtn = new UserRequest();
        rtn.setTicket(ticket);
        rtn.setContentType(contentType);
        rtn.setMethod(method);
//        rtn.setContent(content);

        URI uri = URI.create(url);
        rtn.setPath(uri.getPath());
        rtn.setQuery(uri.getQuery());

        return rtn;
    }

    public static String getFromQuery(String query, String key) {
        Map<String, List<String>> parameters = new QueryStringDecoder(query).parameters();
        List<String> tickets = parameters.get(key);
        if (tickets != null && tickets.size() > 0) {
            return tickets.get(0);
        }
        return null;
    }

    public static ApiInfo getApiInfo(String path, String method) {
        ApiInfo httpApi = new ApiInfo();
        httpApi.setPath(path);
        httpApi.setMethod(method);
        httpApi.setAuthorizedRoles(new String[]{"users", "admins"});
        httpApi.setForbiddenRoles(new String[]{"xyz"});
        httpApi.setBackendHost("http://localhost:8081");
        httpApi.setBackendPath("/abc/test");
        httpApi.setWrapperResponse(false);
        return httpApi;
    }

    public static User getUser(String ticket) {
        User user = new User();
        user.setEmail("cloudbeer@gmail.com");
        user.setId(1L);
        user.setOpenId("dddd");
        user.setMobile("15820468866");
        user.setRoles(new String[]{"admins", "leaders"});
        return user;
    }

    public static FullHttpRequest copyRequest(FullHttpRequest srcRequest, String uri) {
//        srcRequest.content().retain();
//        FullHttpRequest request = srcRequest.copy();
//        request.setUri(uri);
//
        FullHttpRequest request = new DefaultFullHttpRequest(
                srcRequest.protocolVersion(),
                srcRequest.method(),
                uri
        );
//
//
        HttpHeaders headers = srcRequest.headers();
        if (!headers.isEmpty()) {
            for (CharSequence name : headers.names()) {
//                System.out.println(name + ":" + headers.getAll(name));
                request.headers().set(name, headers.getAll(name));
            }
        }
        ByteBuf data = srcRequest.content();
        if (data != null) {
            request.content().clear().writeBytes(data);
            data.release();
        }


        return request;
    }

}
