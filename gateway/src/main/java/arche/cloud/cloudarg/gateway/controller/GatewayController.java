package arche.cloud.cloudarg.gateway.controller;

import arche.cloud.cloudarg.gateway.model.HttpApi;
import arche.cloud.cloudarg.gateway.model.User;
import arche.cloud.cloudarg.gateway.model.UserRequest;
import arche.cloud.cloudarg.gateway.utils.RBACUtil;
import arche.cloud.cloudarg.gateway.utils.RequestUtil;
import arche.cloud.cloudarg.gateway.utils.ResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


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

/**
 *
 */
@RestController
public class GatewayController {

    @RequestMapping("/**")
    public Object gateway(final HttpServletRequest request) {
        UserRequest userRequest = RequestUtil.parse(request);
        System.out.println(userRequest);
        HttpApi apiInfo = RequestUtil.getApiInfo(userRequest.getPath(), userRequest.getMethod());

        if (apiInfo == null) {
            return ResponseUtil.generateResponse(HttpStatus.NOT_FOUND, "Api not found.");
        }

        // An open API
        if (apiInfo.getAuthorizedRoles() != null || apiInfo.getForbiddenRoles() != null) {
            final String ticket = userRequest.getTicket();
            if (ticket == null) {
                return ResponseUtil.generateResponse(HttpStatus.UNAUTHORIZED, "Ticket is required.");
            }
            User user = RequestUtil.getUser(ticket);
            boolean canPass = RBACUtil.pass(user.getRoles(), apiInfo.getAuthorizedRoles(), apiInfo.getForbiddenRoles());

            if (!canPass) {
                return ResponseUtil.generateResponse(HttpStatus.UNAUTHORIZED, "Not Authorized.");
            }

            return ResponseUtil.generateResponse(HttpStatus.OK, user);
        }

        return ResponseUtil.generateResponse(HttpStatus.OK, "directPass");
    }
}
