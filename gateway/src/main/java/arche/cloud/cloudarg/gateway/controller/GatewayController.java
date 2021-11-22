package arche.cloud.cloudarg.gateway.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

@RestController
public class GatewayController {

    @RequestMapping("/**")
    public Object gateway(final HttpServletRequest request) {
        String url = request.getRequestURI();
        String contentType = request.getContentType();
        String method = request.getMethod();
        if (url != null) {
            url = url.toLowerCase(Locale.ROOT);
        }
        if (contentType != null) {
            contentType = contentType.toLowerCase(Locale.ROOT);
        }
        if (method != null) {
            method = method.toUpperCase(Locale.ROOT);
        }





        System.out.println(contentType);
        System.out.println(url);
        System.out.println(method);

//        try {
//            Map a = new Gson().fromJson(request.getReader(), Map.class);
//            System.out.println(a.toString());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


//        try (InputStream is = request.getInputStream();) {
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        final String ticket = request.getParameter("ticket");

        return "ticket is: " + ticket;
    }
}
