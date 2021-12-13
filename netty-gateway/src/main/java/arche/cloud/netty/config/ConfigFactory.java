package arche.cloud.netty.config;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import arche.cloud.netty.utils.GsonUtil;

public class ConfigFactory {
    public static Config config;

    public static void load() {
        InputStream is = ConfigFactory.class.getClassLoader().getResourceAsStream("config.json");
        assert is != null;
        InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
        config = GsonUtil.deserialize(reader, Config.class);

    }
}
