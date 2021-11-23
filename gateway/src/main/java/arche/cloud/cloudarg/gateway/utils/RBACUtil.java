package arche.cloud.cloudarg.gateway.utils;

import arche.cloud.cloudarg.gateway.model.User;

import java.util.Arrays;

public class RBACUtil {

    public static boolean pass(String url, String ticket) {
        return true;
    }

    public static User getUser(String ticket) {
        return null;
    }

    /**
     * Determine user role can pass
     *
     * @param myRoles
     * @param authorizedRoles
     * @param forbiddenRoles
     * @return
     */
    public static boolean pass(String[] myRoles, String[] authorizedRoles, String[] forbiddenRoles) {
        if (forbiddenRoles != null) {
            for (String r : forbiddenRoles) {
                if (Arrays.asList(myRoles).contains(r)) {
                    // Arrays.stream(myRoles).noneMatch(r::equals);
                    return false;
                }
            }
        }
        if (authorizedRoles != null) {
            for (String r : authorizedRoles) {
                if (Arrays.asList(myRoles).contains(r)) {
                    return true;
                }
            }
        }
        return false;
    }
}
