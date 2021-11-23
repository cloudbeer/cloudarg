package arche.cloud.netty.utils;


import arche.cloud.netty.model.HttpApi;
import arche.cloud.netty.model.TicketWrapper;
import arche.cloud.netty.model.User;
import arche.cloud.netty.model.UserRequest;
import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.util.CharsetUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class RequestUtil {
    /**
     * 从一个请求中获取必要的参数
     *
     * @param request HttpServletRequest 实例
     * @return 请求模型
     */
    public static UserRequest parse(FullHttpRequest request) {
        String uri = StringUtil.toLowerCase(request.uri(), null);
        HttpHeaders headers = request.headers();
        String contentType = StringUtil.toLowerCase(headers.get("Content-Type"), null);
        String method = StringUtil.toUpperCase(request.method().name(), "GET");
        String ticket = null;

        if ("GET".contentEquals(method)) {
            ticket = getTicketFromQuery(uri);
        } else {
            if (contentType.contains("json")) {
                ByteBuf contentBuf = request.content();
                String content = contentBuf.toString(CharsetUtil.UTF_8);
                Gson gson = new Gson();
                TicketWrapper wrapper = gson.fromJson(content, TicketWrapper.class);
                ticket = wrapper.getTicket();
            } else {
                HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(new DefaultHttpDataFactory(false), request);
                List<InterfaceHttpData> datas = decoder.getBodyHttpDatas();
                for (InterfaceHttpData data : datas) {
                    if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                        try {
                            String name = data.getName();
                            if ("ticket".equals(name)){
                                ticket =  ((Attribute) data).getString();
                                break;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                decoder.destroy();
            }

        }

        UserRequest rtn = new UserRequest();
        rtn.setTicket(ticket);
        rtn.setPath(StringUtil.getPathName(uri));
        rtn.setContentType(contentType);
        rtn.setMethod(method);
        return rtn;
    }

    public static String getTicketFromQuery(String query) {
        Map<String, List<String>> parameters = new QueryStringDecoder(query).parameters();
        List<String> tickets = parameters.get("ticket");
        if (tickets != null && tickets.size() > 0) {
            return tickets.get(0);
        }
        return null;
    }

    public static HttpApi getApiInfo(String path, String method) {
        HttpApi httpApi = new HttpApi();
        httpApi.setPath(path);
        httpApi.setMethod(method);
        httpApi.setAuthorizedRoles(new String[]{"users", "admins"});
        httpApi.setForbiddenRoles(new String[]{"xyz"});
        httpApi.setBackendHost("http://localhost:8081");
        httpApi.setBackendPath("/abc/test");
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


}
