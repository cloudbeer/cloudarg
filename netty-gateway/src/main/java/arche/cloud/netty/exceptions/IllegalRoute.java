package arche.cloud.netty.exceptions;

import io.netty.handler.codec.http.HttpResponseStatus;

public class IllegalRoute extends Responsable {
  public IllegalRoute() {
    super(HttpResponseStatus.BAD_REQUEST, "Illegal routing");
  }
}
