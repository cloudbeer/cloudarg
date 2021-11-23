package arche.cloud.cloudarg.gateway.model;

/**
 * Request from user (browser, app, opeapi)
 */
public class UserRequest {
    private String method;
    private String contentType;
    private String path;
    private String ticket;

    public String toString(){
        return "{" +
                "method: " + method + ", " +
                "contentType: " + contentType + ", " +
                "path: " + path + ", " +
                "ticket: " + ticket +
                "}";
    }
    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }
}
