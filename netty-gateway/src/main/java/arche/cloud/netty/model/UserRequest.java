package arche.cloud.netty.model;

import arche.cloud.netty.utils.GsonUtil;
import io.netty.buffer.ByteBuf;

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
  private String requestId;

  public String toString() {
    return GsonUtil.toString(this);
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

  public String getRequestId() {
    return requestId;
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }

  public String logInfo() {
    return "request-id:[" + requestId + "] - " +
        method + " " + path +
        ((query != null) ? "?" + query : "");
  }
}
