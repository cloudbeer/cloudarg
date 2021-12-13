package arche.cloud.netty.db;

import arche.cloud.netty.config.Config;
import arche.cloud.netty.config.ConfigFactory;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

public class RedisUtil {


  public static void append(String key, String value, long expireSeconds) {
    RedisClient redisClient = RedisClient.create(ConfigFactory.config.getRedisUri());
    StatefulRedisConnection<String, String> connection = redisClient.connect();
    RedisCommands<String, String> syncCommands = connection.sync();
    syncCommands.sadd(key, value);
    if (expireSeconds > 0) {
      syncCommands.expire(key, expireSeconds);
    }
    connection.close();
    redisClient.shutdown();
  }

  public static void saveRedis(String key, String value, long expireSeconds) {

    RedisClient redisClient = RedisClient.create(ConfigFactory.config.getRedisUri());
    StatefulRedisConnection<String, String> connection = redisClient.connect();
    RedisCommands<String, String> syncCommands = connection.sync();

    syncCommands.set(key, value);
    if (expireSeconds > 0) {
      syncCommands.expire(key, expireSeconds);
    }

    connection.close();
    redisClient.shutdown();
  }

  public static String fromRedis(String key) {
    RedisClient redisClient = RedisClient.create(ConfigFactory.config.getRedisUri());
    StatefulRedisConnection<String, String> connection = redisClient.connect();
    RedisCommands<String, String> syncCommands = connection.sync();

    String val = syncCommands.get(key);

    // System.err.println("[redis] " + key + ": " + val);

    connection.close();
    redisClient.shutdown();
    return val;
  }
}
