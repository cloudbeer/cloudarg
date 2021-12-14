package arche.cloud.netty.utils;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class CIDR6Util {
  private CIDR6Util() {
    throw new IllegalStateException("Utility class");
  }

  public static String[] toStringRange(String cidr) {
    InetAddress[] range = toInteAddressRange(cidr);
    if (range.length <= 1) {
      return new String[0];
    }
    return Stream.of(range).map(InetAddress::getHostAddress).toArray(String[]::new);
  }

  public static InetAddress[] toInteAddressRange(String cidr) {
    int index = cidr.indexOf("/");

    try {
      if (index <= 0) {
        return new InetAddress[] { InetAddress.getByName(cidr), InetAddress.getByName(cidr) };
      }
      String addressPart = cidr.substring(0, index);
      String networkPart = cidr.substring(index + 1);
      InetAddress inetAddress;
      InetAddress startAddress;
      InetAddress endAddress;
      int prefixLength;
      inetAddress = InetAddress.getByName(addressPart);

      prefixLength = Integer.parseInt(networkPart);

      ByteBuffer maskBuffer;
      int targetSize;
      if (inetAddress.getAddress().length == 4) {
        maskBuffer = ByteBuffer
            .allocate(4)
            .putInt(-1);
        targetSize = 4;
      } else {
        maskBuffer = ByteBuffer.allocate(16)
            .putLong(-1L)
            .putLong(-1L);
        targetSize = 16;
      }

      BigInteger mask = (new BigInteger(1, maskBuffer.array())).not().shiftRight(prefixLength);

      ByteBuffer buffer = ByteBuffer.wrap(inetAddress.getAddress());
      BigInteger ipVal = new BigInteger(1, buffer.array());

      BigInteger startIp = ipVal.and(mask);
      BigInteger endIp = startIp.add(mask.not());

      byte[] startIpArr = toBytes(startIp.toByteArray(), targetSize);
      byte[] endIpArr = toBytes(endIp.toByteArray(), targetSize);

      startAddress = InetAddress.getByAddress(startIpArr);
      endAddress = InetAddress.getByAddress(endIpArr);

      return new InetAddress[] { startAddress, endAddress };
    } catch (UnknownHostException e) {
      e.printStackTrace();
      return new InetAddress[0];
    }

  }

  private static BigInteger toBigInt(String ip) {
    try {
      InetAddress inetAddress = InetAddress.getByName(ip);
      ByteBuffer buffer = ByteBuffer.wrap(inetAddress.getAddress());
      return new BigInteger(1, buffer.array());
    } catch (UnknownHostException e) {
      e.printStackTrace();
      return BigInteger.ZERO;
    }

  }

  public static BigInteger[] toBigIntRange(String cidr) {
    int index = cidr.indexOf("/");

    try {
      if (index <= 0) {
        return new BigInteger[] { toBigInt(cidr), toBigInt(cidr) };
      }
      String addressPart = cidr.substring(0, index);
      String networkPart = cidr.substring(index + 1);
      InetAddress inetAddress;
      int prefixLength;
      inetAddress = InetAddress.getByName(addressPart);

      prefixLength = Integer.parseInt(networkPart);

      ByteBuffer maskBuffer;
      if (inetAddress.getAddress().length == 4) {
        maskBuffer = ByteBuffer
            .allocate(4)
            .putInt(-1);
      } else {
        maskBuffer = ByteBuffer.allocate(16)
            .putLong(-1L)
            .putLong(-1L);
      }

      BigInteger mask = (new BigInteger(1, maskBuffer.array())).not().shiftRight(prefixLength);

      ByteBuffer buffer = ByteBuffer.wrap(inetAddress.getAddress());
      BigInteger ipVal = new BigInteger(1, buffer.array());

      BigInteger startIp = ipVal.and(mask);
      BigInteger endIp = startIp.add(mask.not());

      return new BigInteger[] { startIp, endIp };
    } catch (UnknownHostException e) {
      e.printStackTrace();
      return new BigInteger[0];
    }

  }

  public static boolean inRange(String ipAddress, String cidr) {
    try {
      BigInteger[] range = toBigIntRange(cidr);
      InetAddress address = InetAddress.getByName(ipAddress);
      BigInteger target = new BigInteger(1, address.getAddress());

      if (range.length <= 1) {
        return false;
      }
      int st = range[0].compareTo(target);
      int te = target.compareTo(range[1]);

      return (st < 0 || st == 0) && (te < 0 || te == 0);
    } catch (UnknownHostException e) {
      e.printStackTrace();
      return false;
    }
  }

  public static boolean inRange(String ipAddress, String[] cidrs) {
    if (cidrs == null) {
      return false;
    }
    for (String cidr : cidrs) {
      if (inRange(ipAddress, cidr)) {
        return true;
      }
    }
    return false;
  }

  private static byte[] toBytes(byte[] array, int targetSize) {
    int counter = 0;
    List<Byte> newArr = new ArrayList<>();
    while (counter < targetSize && (array.length - 1 - counter >= 0)) {
      newArr.add(0, array[array.length - 1 - counter]);
      counter++;
    }

    int size = newArr.size();
    for (int i = 0; i < (targetSize - size); i++) {

      newArr.add(0, (byte) 0);
    }

    byte[] ret = new byte[newArr.size()];
    for (int i = 0; i < newArr.size(); i++) {
      ret[i] = newArr.get(i);
    }
    return ret;
  }
}
