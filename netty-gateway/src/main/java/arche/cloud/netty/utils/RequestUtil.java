package arche.cloud.netty.utils;

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
        String url = request.uri();

        HttpHeaders headers = request.headers();
        String contentType = "application/json";
        String authorization = null;
        if (!headers.isEmpty()) {
            for (CharSequence name : headers.names()) {
                if (name.toString().toLowerCase(Locale.ROOT).equals("content-type")) {
                    contentType = headers.get(name);
                    // break;
                }
                if (name.toString().toLowerCase(Locale.ROOT).equals("authorization")) {
                    authorization = headers.get(name);
                    // break;
                }
            }
        }

        String method = StringUtil.toUpperCase(request.method().name(), "GET");

        // 约定 ticket 必须放在 url 的 query 的 __ticket 中，不能放在其他地方
        String ticket = getValueFromQuery(url, "__ticket");
        if (ticket == null && authorization != null && authorization.length() > 8) {
            ticket = authorization.substring(7); // 去掉 "bearer "
        }
        // System.out.println("ticket at:" + ticket + "|" + authorization);

        UserRequest rtn = new UserRequest();
        rtn.setTicket(ticket);
        rtn.setContentType(contentType);
        rtn.setMethod(method);

        URI uri = URI.create(url);
        rtn.setPath(uri.getPath());
        rtn.setQuery(uri.getQuery());

        return rtn;
    }

    public static String getValueFromQuery(String query, String key) {
        Map<String, List<String>> parameters = new QueryStringDecoder(query).parameters();
        List<String> tickets = parameters.get(key);
        if (tickets != null && tickets.size() > 0) {
            return tickets.get(0);
        }
        return null;
    }

    // public static ApiInfo getApiInfo(String path, String method) {
    // ApiInfo httpApi = new ApiInfo();
    // httpApi.setPath(path);
    // httpApi.setMethod(method);
    // httpApi.setAuthorizedRoles(new String[]{"users", "admins"});
    // httpApi.setForbiddenRoles(new String[]{"xyz"});
    // httpApi.setBackendHost("http://localhost:8081");
    // httpApi.setBackendPath("/abc/test");
    // httpApi.setWrapperResponse(false);
    // return httpApi;
    // }

    // public static User getUser(String ticket) {
    // User user = new User();
    // user.setEmail("cloudbeer@gmail.com");
    // user.setId(1L);
    // user.setOpenId("dddd");
    // user.setMobile("15820468866");
    // user.setRoles(new String[]{"admins", "leaders"});
    // return user;
    // }

    public static FullHttpRequest copyRequest(FullHttpRequest srcRequest, String uri) {
        FullHttpRequest request = new DefaultFullHttpRequest(
                srcRequest.protocolVersion(),
                srcRequest.method(),
                uri);

        HttpHeaders headers = srcRequest.headers();
        if (!headers.isEmpty()) {
            for (CharSequence name : headers.names()) {
                request.headers().set(name, headers.getAll(name));
            }
        }
        ByteBuf data = srcRequest.content();
        if (data != null) {
            request.content().clear().writeBytes(data);
            srcRequest.release();
        }

        return request;
    }

}
