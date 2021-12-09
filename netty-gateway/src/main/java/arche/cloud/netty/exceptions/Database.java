package arche.cloud.netty.exceptions;

import io.netty.handler.codec.http.HttpResponseStatus;

public class Database extends Responsable {
  public Database() {
    super(HttpResponseStatus.INTERNAL_SERVER_ERROR, "Database error.");
  }

  public Database(String msg) {
    super(HttpResponseStatus.INTERNAL_SERVER_ERROR, msg);
  }
}
