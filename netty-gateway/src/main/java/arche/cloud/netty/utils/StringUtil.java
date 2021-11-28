package arche.cloud.netty.utils;

import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

    public static byte[] getSHA(@NotNull String input) throws NoSuchAlgorithmException {
        // Static getInstance method is called with hashing SHA
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        // digest() method called
        // to calculate message digest of an input
        // and return array of byte
        return md.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    @NotNull
    public static String toHexString(byte[] hash) {
        BigInteger number = new BigInteger(1, hash);

        // Convert message digest into hex value
        StringBuilder hexString = new StringBuilder(number.toString(16));

        // Pad with leading zeros
        while (hexString.length() < 32) {
            hexString.insert(0, '0');
        }

        return hexString.toString();
    }
}
