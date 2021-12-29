package arche.cloud.netty.utils;

import arche.cloud.netty.exceptions.Responsable;
import arche.cloud.netty.exceptions.NotAuthorized;
import arche.cloud.netty.model.User;

import java.util.Arrays;

public class RBACUtil {
  private RBACUtil() {
    throw new IllegalStateException("Utility class");
  }

  public static boolean pass(String url, String ticket) {
    return true;
  }

  public static User getUser(String ticket) {
    return null;
  }

  /**
   * Determine user role can pass
   *
   * @param myRoles         用户角色
   * @param authorizedRoles route 允许的角色
   * @param forbiddenRoles  route 禁止的角色
   */
  public static boolean pass(String[] myRoles, String[] authorizedRoles, String[] forbiddenRoles) throws Responsable {
    if (myRoles == null) {
      return false;
    }
    if (forbiddenRoles != null) {
      for (String r : forbiddenRoles) {
        if (Arrays.asList(myRoles).contains(r)) {
          // Arrays.stream(myRoles).noneMatch(r::equals);
          return false;
        }
      }
    }

    boolean passed = false;
    if (authorizedRoles != null) {
      for (String r : authorizedRoles) {
        if (Arrays.asList(myRoles).contains(r)) {
          passed = true;
          break;
        }
      }
    }

    return passed;
  }
}
