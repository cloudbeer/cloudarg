package arche.cloud.netty.exceptions;

import arche.cloud.netty.utils.ResponseUtil;
import com.google.gson.Gson;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class Responsable extends Exception {

  Logger logger = LoggerFactory.getLogger(this.getClass().getName());
  private final HttpResponseStatus status;
  private final String msg;
  private String method;
  private String path;
  private String query;

  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getQuery() {
    return query;
  }

  public void setQuery(String query) {
    this.query = query;
  }

  public Responsable(HttpResponseStatus status, String msg) {
    super(msg);
    this.msg = msg;
    this.status = status;
  }

  public void echo(ChannelHandlerContext ctx, String requestId, String pathInfo, boolean cors) {

    logger.error("request-id:[{}] - {} - {}", requestId, pathInfo, msg);
    HashMap<String, String> headers = new HashMap<>();
    headers.put("tracing_id", requestId);
    if (cors) {
      headers.put("Access-Control-Allow-Origin", "*");
      headers.put("Access-Control-Allow-Methods", "*");
      headers.put("Access-Control-Request-Headers", "*");
    }
    ResponseUtil.wrap(ctx, status, headers, msg);

  }

}
