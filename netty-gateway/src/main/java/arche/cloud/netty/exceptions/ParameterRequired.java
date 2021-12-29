package arche.cloud.netty.exceptions;

import io.netty.handler.codec.http.HttpResponseStatus;

public class ParameterRequired extends Responsable {
  public ParameterRequired(String msg) {
    super(HttpResponseStatus.NOT_ACCEPTABLE, msg);
  }
}
