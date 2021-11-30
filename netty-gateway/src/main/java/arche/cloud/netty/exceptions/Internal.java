package arche.cloud.netty.exceptions;

import io.netty.handler.codec.http.HttpResponseStatus;

public class Internal extends Responsable{
  public Internal(String msg){
    super(HttpResponseStatus.INTERNAL_SERVER_ERROR, msg);
  }
}
