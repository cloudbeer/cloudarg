package arche.cloud.netty.config;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;

public class ConfigFactory {
    public static Config config;

    public static void load() {
        Gson gson = new Gson();
        InputStream is = ConfigFactory.class.getClassLoader().getResourceAsStream("config.json");
        assert is != null;
        InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
        config = gson.fromJson(reader, Config.class);

    }
}
