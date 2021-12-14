package arche.cloud.netty.model;

public class Constants {

  private Constants() {
    throw new IllegalStateException("Utility class");
  }

  public static final String HEADER_REQUEST_ID = "trace_id";
  public static final String HEADER_APP_NAME = "cloudarg";

}
