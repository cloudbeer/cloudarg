package arche.cloud.netty.exceptions;

import io.netty.handler.codec.http.HttpResponseStatus;

public class RouteNotFound extends Responsable {
  public RouteNotFound() {
    super(HttpResponseStatus.NOT_FOUND, "route not found.");
  }
}
