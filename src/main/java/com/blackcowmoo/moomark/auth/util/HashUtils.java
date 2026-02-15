package com.blackcowmoo.moomark.auth.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtils {

  private HashUtils() throws InstantiationException {
    throw new InstantiationException("HashUtils is utility class");
  }

  public static String toSha256(String msg) {
    String result = "";

    try {
      MessageDigest md = MessageDigest.getInstance("SHA256");
      md.update(msg.getBytes());
      byte[] byteData = md.digest();
      StringBuilder sb = new StringBuilder();
      for (byte data : byteData) {
        sb.append(Integer.toString((data & 0xff) + 0x100, 16).substring(1));
      }
      result = sb.toString();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }

    return result;
  }
}
