package arche.cloud.netty.exceptions;

import io.netty.handler.codec.http.HttpResponseStatus;

public class NotAuthorized extends Responsable {
  public NotAuthorized() {
    super(HttpResponseStatus.UNAUTHORIZED, "Authentication failed.");
  }
}
