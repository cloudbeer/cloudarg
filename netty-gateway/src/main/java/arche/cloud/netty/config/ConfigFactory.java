package arche.cloud.netty.config;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.text.StringSubstitutor;
import org.apache.commons.text.lookup.StringLookupFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class ConfigFactory {
    public static Config config;

    public static void load() throws IOException {

        InputStream is = ConfigFactory.class.getClassLoader().getResourceAsStream("config.yaml");
        assert is != null;
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        String strConfig = result.toString(StandardCharsets.UTF_8.name());
        StringSubstitutor stringReplacer = new StringSubstitutor(
                StringLookupFactory.INSTANCE.environmentVariableStringLookup());
        strConfig = stringReplacer.replace(strConfig);

        Yaml yaml = new Yaml(new Constructor(Config.class));
        config = yaml.load(strConfig);

    }
}
