package arche.cloud.netty.exceptions;

import io.netty.handler.codec.http.HttpResponseStatus;

public class BackendNotFound extends Responsable {
  public BackendNotFound() {
    super(HttpResponseStatus.NOT_FOUND, "backend not found.");
  }
}
