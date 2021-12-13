package arche.cloud.netty.utils;

import java.io.Reader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonUtil {
  private GsonUtil() {
    throw new IllegalStateException("Utility class");
  }

  static Gson gson;
  static Gson gsonPretty;

  static {
    gson = new Gson();
    gsonPretty = new GsonBuilder().setPrettyPrinting().create();
  }

  public static String serialize(Object obj) {
    return gson.toJson(obj);
  }

  public static <T> T deserialize(String json, Class<T> type) {
    return gson.fromJson(json, type);
  }

  public static <T> T deserialize(Reader json, Class<T> type) {
    return gson.fromJson(json, type);
  }

  public static String toString(Object obj) {
    return gsonPretty.toJson(obj);
  }

}
