package arche.cloud.cloudarg.gateway.utils;

import java.util.Locale;

public class StringUtil {
    public static String toLowerCase(String input, String defaultValue) {
        if (input == null) {
            return defaultValue;
        }
        return input.toLowerCase(Locale.ROOT);
    }

    public static String toUpperCase(String input, String defaultValue) {
        if (input == null) {
            return defaultValue;
        }
        return input.toUpperCase(Locale.ROOT);
    }

    public static boolean isBlank(String input) {
        return (input == null) || (input.length() == 0);
    }
}
