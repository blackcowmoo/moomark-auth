package com.blackcowmoo.moomark.auth.util;

public class ByteUtil {
  public static byte[] hexToByteArray(String hex) {
    hex = hex.replaceAll("\"", "\\\""); /*" */
    if (hex == null || hex.length() == 0) {
      return null;
    }
    byte[] ba = new byte[hex.length() / 2];
    for (int i = 0; i < ba.length; i++) {
      ba[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
    }
    return ba;
  }
}
