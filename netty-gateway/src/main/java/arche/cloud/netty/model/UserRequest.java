package arche.cloud.netty.model;

import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;

/**
 * Request from user (browser, app, opeapi)
 */
public class UserRequest {
    private String method;
    private String contentType;
    private String path;
    private String ticket;
    private String host;
    private int port;
    private String schema;
    private ByteBuf content;
    private String query;

    public String toString(){
        return "{" +
                "method: " + method + ", " +
                "contentType: " + contentType + ", " +
                "path: " + path + ", " +
                "query: " + query + ", " +
                "content: " + content.toString(CharsetUtil.UTF_8) + ", " +
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

    public ByteBuf getContent() {
        return content;
    }

    public void setContent(ByteBuf content) {
        this.content = content;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
