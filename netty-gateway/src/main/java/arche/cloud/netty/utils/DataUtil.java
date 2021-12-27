package arche.cloud.netty.utils;

import arche.cloud.netty.config.ConfigFactory;
import arche.cloud.netty.db.MysqlDataSource;
import arche.cloud.netty.db.RedisUtil;
import arche.cloud.netty.exceptions.*;
import arche.cloud.netty.model.*;
import com.google.common.base.Strings;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class DataUtil {

  private DataUtil() {
    throw new IllegalStateException("Utility class");
  }

  static Logger logger = LoggerFactory.getLogger(DataUtil.class);

  public static final Base64.Decoder decoder;
  private static final Random random;

  static {
    decoder = Base64.getDecoder();
    random = new Random();
  }

  public static String computeOpenId(String ticket) throws Exception {
    Ticket ticket1;
    try {
      String ticketJson = new String(decoder.decode(ticket), StandardCharsets.UTF_8);
      ticket1 = GsonUtil.deserialize(ticketJson, Ticket.class);
    } catch (Exception ex) {
      throw new Exception("authorized failed.");
    }
    String sign = ticket1.getSign();
    long expiresIn = ticket1.getExpiresIn();
    String openId = ticket1.getOpenId();
    long datetime = System.currentTimeMillis();
    if (datetime > expiresIn * 1000) {
      throw new Exception("ticket expired.");
    }
    String src = "{\"open_id\":\"" + openId + "\",\"expires_in\":" + expiresIn + "}";
    String cSign = computeSign(src + ConfigFactory.config.getTicketSecret());
    if (!sign.equals(cSign)) {
      throw new Exception("authorized failed.");
    }

    return openId;
  }

  @NotNull
  public static String computeSign(String input) throws NoSuchAlgorithmException {
    return StringUtil.toHexString(StringUtil.getSHA(input));
  }

  public static User getUser(String ticket) throws Responsable {
    String ticketKey = StringUtil.hashKey("t", ticket);
    try {
      User user = getUserFromRedis(ticketKey);
      if (user != null) {
        return user;
      }
      user = getUserRemote(ConfigFactory.config.getAccountUrl() + "?ticket=" + ticket);
      if (user != null) {
        saveUserToRedis(ticketKey, user);
        return user;
      }
      return null;
    } catch (Exception e) {
      logger.error("Get user exception.", e);
      // e.printStackTrace();
      throw new NotAuthorized();
    }
  }

  public static User getUserFromRedis(String ticketKey) {
    String userValue = RedisUtil.fromRedis(ticketKey);
    if (userValue != null) {
      // System.err.println("Get user from redis");
      return GsonUtil.deserialize(userValue, User.class);
    }
    return null;
  }

  private static void saveUserToRedis(String ticketKey, User user) {
    String userValue = GsonUtil.serialize(user);
    RedisUtil.saveRedis(ticketKey, userValue, 7200);
  }

  public static User getUserRemote(String url) throws Exception {
    // System.err.println("get User From remote");
    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder()
        .url(url)
        .build();

    try (Response response = client.newCall(request).execute()) {
      // assert response.body() != null;
      String json = Objects.requireNonNull(response.body()).string();
      // System.out.println(json);
      UserWrapper resp = GsonUtil.deserialize(json, UserWrapper.class);
      if (resp.isSuccess()) {
        return resp.getData();
      } else {
        throw new Exception("can not obtain user.");

      }
    } catch (IOException e) {
      e.printStackTrace();
      throw e;
    }

  }

  public static boolean isArrayEmpty(Object[] s) {
    return s == null || s.length == 0;
  }

  public static Route getRouteInfo(String path) throws Responsable {

    String[] paths = path.split("/");
    int size = paths.length;
    if (size < 3) {
      throw new IllegalRoute();
    }

    String pathKey = StringUtil.hashKey("p", path);
    Route exacRoute = getRouteFromRedis(pathKey);
    if (exacRoute != null) {
      return exacRoute;
    }

    // jing准匹配
    exacRoute = getRouteFromDB(path);
    if (exacRoute == null) {
      // 开始模糊匹配
      exacRoute = getBestRouteFromDB(path, size);
    }
    if (exacRoute == null) {
      throw new RouteNotFound();
    }

    saveRouteToRedis(pathKey, exacRoute);

    return exacRoute;
  }

  public static Route getRouteFromRedis(String key) {
    String userValue = RedisUtil.fromRedis(key);
    if (userValue != null) {
      return GsonUtil.deserialize(userValue, Route.class);
    }
    return null;
  }

  /**
   * 将用户访问的 route 加入缓存
   *
   * @param key   redis 的 key
   * @param route 路由
   */
  private static void saveRouteToRedis(String key, Route route) {
    String userValue = GsonUtil.serialize(route);
    RedisUtil.saveRedis(key, userValue, 0);
    // 反向存储缓存，将 DB 中的 route 的使用到的 key 存储在缓存中。方便更新路由配置的时候可以清理缓存。
    RedisUtil.append("r-" + route.getId(), key, 0);
  }

  public static Route getBestRouteFromDB(String path, int size) throws Responsable {
    String tarPath = path;
    for (int idx = 0; idx < size - 3; idx++) {
      tarPath = tarPath.substring(0, tarPath.lastIndexOf('/'));
      Route route = getRouteFromDB(tarPath + "/");
      if (route != null) {
        return route;
      }
    }
    return null;
  }

  public static Route getRouteFromDB(String path) throws Responsable {
    try (Connection conn = MysqlDataSource.getConnection()) {
      Route route = null;
      String sqlSelectRoute = "select * from v_route_project where path_hash=CRC32(?)";
      // System.err.println(sqlSelectRoute);
      // System.err.println(path);

      try (PreparedStatement psRoute = conn.prepareStatement(sqlSelectRoute)) {
        psRoute.setString(1, path);
        ResultSet rs = psRoute.executeQuery();
        while (rs.next()) {
          route = new Route();
          route.setId(rs.getLong("id"));
          route.setPath(rs.getString("path"));
          route.setFullPath(rs.getString("full_path"));
          route.setWrapper(rs.getInt("wrapper"));
          route.setCors(rs.getInt("cors"));
          String blackList = rs.getString("black_list");
          String whiteList = rs.getString("white_list");
          if (!Strings.isNullOrEmpty(blackList)) {
            route.setBlackList(blackList.split("[\r?\n\s*]+"));
          }
          if (!Strings.isNullOrEmpty(whiteList)) {
            route.setWhiteList(whiteList.split("[\r?\n\s*]+"));
          }
          route.setMock(rs.getShort("mock"));
          route.setMockContent(rs.getString("mock_content"));
          route.setMockContentType(rs.getString("mock_content_type"));
          route.setMockContentUrl(rs.getString("mock_content_url"));
          route.setRateLimit(rs.getInt("rate_limit"));
          route.setFailover(rs.getShort("failover"));
          route.setFailoverContent(rs.getString("failover_content"));
          route.setFailoverContentType(rs.getString("failover_content_type"));
          route.setFailoverUrl(rs.getString("failover_url"));
        }
        rs.close();
      } catch (SQLException ex) {
        ex.printStackTrace();
        throw new Database(ex.getLocalizedMessage());
      }
      if (route == null) {
        return null;
      }

      String sqlSelectRoles = "select * from route_role where route_id=?";
      PreparedStatement psRouteRoles = conn.prepareStatement(sqlSelectRoles);
      psRouteRoles.setLong(1, route.getId());
      ArrayList<String> aRoles = new ArrayList<>();
      ArrayList<String> fRoles = new ArrayList<>();

      try (ResultSet rs = psRouteRoles.executeQuery()) {
        while (rs.next()) {
          int type = rs.getInt("type");
          if (type == 1) {
            aRoles.add(rs.getString("role"));
          } else {
            fRoles.add(rs.getString("role"));
          }
        }
      } catch (SQLException e) {
        e.printStackTrace();
        throw new Database(e.getLocalizedMessage());
      }
      psRouteRoles.close();
      route.setAuthorizedRoles(aRoles.toArray(String[]::new));
      route.setForbiddenRoles(fRoles.toArray(String[]::new));

      String sqlSelectBack = "select * from backend where route_id=?";
      PreparedStatement psBackend = conn.prepareStatement(sqlSelectBack);
      psBackend.setLong(1, route.getId());

      try (ResultSet rs = psBackend.executeQuery()) {

        while (rs.next()) {
          Backend b = new Backend();
          route.getBackends().add(b);
          b.setBodyPattern(rs.getString("body_pattern"));
          b.setHeaderPattern(rs.getString("header_pattern"));
          b.setPathPattern(rs.getString("path_pattern"));
          b.setQueryPattern(rs.getString("query_pattern"));
          b.setEnv(rs.getString("env"));
          b.setHost(rs.getString("host"));
          b.setPath(rs.getString("path"));
          b.setPort(rs.getInt("port"));
          b.setId(rs.getLong("id"));
          b.setSchema(rs.getString("schema"));
          b.setWeight(rs.getInt("weight"));
        }
      } catch (SQLException e2) {
        e2.printStackTrace();
        throw new Database(e2.getLocalizedMessage());
      }
      psBackend.close();

      conn.close();
      return route;
    } catch (SQLException e3) {
      e3.printStackTrace();
      throw new Database(e3.getLocalizedMessage());
    }
  }

  public static Backend chooseBackend(Route route, UserRequest userRequest) throws Responsable {

    List<Backend> backends = route.getBackends();
    int backendSize = backends.size();
    if (backendSize == 0) {
      throw new BackendNotFound();
    }
    if (backendSize == 1) {
      return backends.get(0);
    }
    // TODO: 简化了 backend 选择，复杂逻辑日后再做：各种 pattern
    int[] weights = new int[backendSize];

    int idx = 0;
    int preWeight = 0;
    for (Backend backend : route.getBackends()) {
      preWeight += backend.getWeight();
      weights[idx] = preWeight;
      idx++;
    }
    int rdmWeight = random.nextInt(0, preWeight);
    int chooseIdx = 0;
    for (int tIdx = 0; tIdx < weights.length; tIdx++) {
      if (rdmWeight <= weights[tIdx]) {
        chooseIdx = tIdx;
        break;
      }
    }
    return backends.get(chooseIdx);
  }

}