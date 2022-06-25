package com.blackcowmoo.moomark.auth.util;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RsaUtil {
  private static KeyFactory keyFactory = null;

  public static PublicKey buildPublicKey(String publicKeyString) throws Exception {
    X509EncodedKeySpec ukeySpec = new X509EncodedKeySpec(publicKeyString.getBytes());
    return getKeyFactory().generatePublic(ukeySpec);
  }

  public static PrivateKey buildPrivateKey(String privateKeyString) throws Exception {
    PKCS8EncodedKeySpec rkeySpec = new PKCS8EncodedKeySpec(privateKeyString.getBytes());
    return getKeyFactory().generatePrivate(rkeySpec);
  }

  private static KeyFactory getKeyFactory() throws Exception {
    if (keyFactory == null) {
      keyFactory = KeyFactory.getInstance("RSA");
    }

    return keyFactory;
  }
}
