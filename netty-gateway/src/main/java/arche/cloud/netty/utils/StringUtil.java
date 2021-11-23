package arche.cloud.netty.utils;

import java.util.Locale;

public class StringUtil {
    public static String toLowerCase(CharSequence input, String defaultValue) {
        if (input == null) {
            return defaultValue;
        }
        return input.toString().toLowerCase(Locale.ROOT);
    }

    public static String toUpperCase(CharSequence input, String defaultValue) {
        if (input == null) {
            return defaultValue;
        }
        return input.toString().toUpperCase(Locale.ROOT);
    }

    public static boolean isBlank(String input) {
        return (input == null) || (input.length() == 0);
    }

    public static String getPathName(String uri){
        return uri.substring(0, uri.indexOf('?'));
    }
}
