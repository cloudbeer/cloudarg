package arche.cloud.netty.utils;

import arche.cloud.netty.config.ConfigFactory;
import arche.cloud.netty.db.MysqlDataSource;
import arche.cloud.netty.model.*;
import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Objects;
import java.util.Random;

public class DataUtil {

    public static Base64.Decoder decoder;

    static {
        decoder = Base64.getDecoder();
    }

    public static String computeOpenId(String ticket) throws Exception {
        Ticket ticket1;
        try {
            String ticketJson = new String(decoder.decode(ticket), StandardCharsets.UTF_8);
            Gson gson = new Gson();
            ticket1 = gson.fromJson(ticketJson, Ticket.class);
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


    public static User getUser(String ticket) throws Exception {
        return getUserRemote(ConfigFactory.config.getAccountUrl() + "?ticket=" + ticket);
    }

    public static User getUserRemote(String url) throws Exception {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
//            assert response.body() != null;
            String json = Objects.requireNonNull(response.body()).string();
//            System.out.println(json);
            Gson gson = new Gson();
            UserWrapper resp = gson.fromJson(json, UserWrapper.class);
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

    public static Route getRouteInfo(String path) throws Exception {
        return getRouteFromDB(path);
    }

    public static Route getRouteFromDB(String path) throws Exception {
        try (Connection conn = MysqlDataSource.getConnection()) {
            Route route = null;
            String sqlSelectRoute = "select * from v_route_project where full_path=?";
            PreparedStatement psRoute = conn.prepareStatement(sqlSelectRoute);
            psRoute.setString(1, path);
            try (ResultSet rs = psRoute.executeQuery()) {
                while (rs.next()) {
                    route = new Route();
                    route.setId(rs.getLong("id"));
                    route.setPath(rs.getString("path"));
                    route.setFullPath(rs.getString("full_path"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            psRoute.close();

            if (route != null) {

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
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                psBackend.close();

            }
            conn.close();
            return route;
        }
    }

    public static Backend chooseBackend(Route route, UserRequest userRequest) {
        //TODO: 简化了 backend 选择，复杂逻辑日后再做
        ArrayList<Backend> backends = route.getBackends();
        int[] weights = new int[backends.size()];
        int idx = 0, preWeight = 0;
        for (Backend backend : route.getBackends()) {
            preWeight += backend.getWeight();
            weights[idx] = preWeight;
            idx++;
        }
        int rdmWeight = new Random().nextInt(0, preWeight);
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