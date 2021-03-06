package arche.cloud.netty.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CIDRUtil {
  private CIDRUtil() {
    throw new IllegalStateException("Utility class");
  }

  public static List<String> toCidr(String startIp, String endIp) {

    if (startIp == null || startIp.length() < 8 ||
        endIp == null || endIp.length() < 8)
      return Collections.emptyList();
    long start = toLong(startIp);
    long end = toLong(endIp);
    // check parameters
    if (start > end)
      return Collections.emptyList();

    List<String> result = new ArrayList<>();
    while (start <= end) {
      // identify the location of first 1's from lower bit to higher bit of start IP
      // e.g. 00000001.00000001.00000001.01101100, return 4 (100)
      long locOfFirstOne = start & (-start);
      int maxMask = 32 - (int) (Math.log(locOfFirstOne) / Math.log(2));

      // calculate how many IP addresses between the start and end
      // e.g. between 1.1.1.111 and 1.1.1.120, there are 10 IP address
      // 3 bits to represent 8 IPs, from 1.1.1.112 to 1.1.1.119 (119 - 112 + 1 = 8)
      double curRange = Math.log(end - start + 1) / Math.log(2);
      int maxDiff = 32 - (int) Math.floor(curRange);

      // why max?
      // if the maxDiff is larger than maxMask
      // which means the numbers of IPs from start to end is smaller than mask range
      // so we can't use as many as bits we want to mask the start IP to avoid exceed
      // the end IP
      // Otherwise, if maxDiff is smaller than maxMask, which means number of IPs is
      // larger than mask range
      // in this case we can use maxMask to mask as many as IPs from start we want.
      maxMask = Math.max(maxDiff, maxMask);

      // Add to results
      String ip = toIP(start);
      result.add(ip + "/" + maxMask);
      // We have already included 2^(32 - maxMask) numbers of IP into result
      // So the next round start must add that number
      start += Math.pow(2, (32 - maxMask));
    }
    return result;
  }

  public static long toLong(String strIP) {
    String[] ipSegs = strIP.split("\\.");
    long res = 0;
    for (int i = 0; i < 4; i++) {
      res += Long.parseLong(ipSegs[i]) << (8 * (3 - i));
    }
    return res;
  }

  public static String toIP(long longIP) {

    return (longIP >>> 24) + "." +
        ((longIP & 0x00FFFFFF) >>> 16) + "." +
        ((longIP & 0x0000FFFF) >>> 8) + "." +
        (longIP & 0x000000FF);
  }
}
