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

    public static boolean isTextContentType(String contentType){
        String[] keys = new String[]{"text", "json", "html"};
        for (String k:keys){
            if (contentType.contains(k)) {
                return true;
            }
        }
        return false;
    }

}
