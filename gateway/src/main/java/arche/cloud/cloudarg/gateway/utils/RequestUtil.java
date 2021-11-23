package arche.cloud.cloudarg.gateway.utils;

import arche.cloud.cloudarg.gateway.model.HttpApi;
import arche.cloud.cloudarg.gateway.model.RequestBodyWrapper;
import arche.cloud.cloudarg.gateway.model.User;
import arche.cloud.cloudarg.gateway.model.UserRequest;
import com.google.gson.Gson;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class RequestUtil {
    /**
     * 从一个请求中获取必要的参数
     * @param request HttpServletRequest 实例
     * @return 请求模型
     */
    public static UserRequest parse(HttpServletRequest request) {
        String path = StringUtil.toLowerCase(request.getRequestURI(), null);
        String contentType = StringUtil.toLowerCase(request.getContentType(), null);
        String method = StringUtil.toUpperCase(request.getMethod(), "GET");
        String ticket = null;
        if ("GET".equals(method)) {
            ticket = request.getParameter("ticket");

        } else {
            if (contentType.contains("json")) {
                Gson gson = new Gson();
                try {
                    RequestBodyWrapper wrapper = gson.fromJson(request.getReader(), RequestBodyWrapper.class);
                    ticket = wrapper.getTicket();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                ticket = request.getParameter("ticket");
            }
        }

        UserRequest rtn = new UserRequest();
        rtn.setTicket(ticket);
        rtn.setPath(path);
        rtn.setContentType(contentType);
        rtn.setMethod(method);
        return rtn;
    }

    public static HttpApi getApiInfo(String path, String method){
        HttpApi httpApi = new HttpApi();
        httpApi.setPath(path);
        httpApi.setMethod(method);
        httpApi.setAuthorizedRoles(new String[]{"users", "admins"});
        httpApi.setForbiddenRoles(new String[]{"xyz"});
        httpApi.setBackendHost("http://localhost:8081");
        httpApi.setBackendPath("/abc/test");
        return null;
    }

    public static User getUser(String ticket){
        User user = new User();
        user.setEmail("cloudbeer@gmail.com");
        user.setId(1L);
        user.setOpenId("dddd");
        user.setMobile("15820468866");
        user.setRoles(new String[]{"admins", "leaders"});
        return user;
    }


}
