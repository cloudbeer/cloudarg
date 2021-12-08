package arche.cloud.netty.exceptions;

import io.netty.handler.codec.http.HttpResponseStatus;

public class NotSupport extends Responsable {
  public NotSupport() {
    super(HttpResponseStatus.NOT_ACCEPTABLE, "Unsupported content.");
  }
}
