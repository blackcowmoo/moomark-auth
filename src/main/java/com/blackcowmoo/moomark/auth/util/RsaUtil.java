package com.blackcowmoo.moomark.auth.util;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RsaUtil {
  private static KeyFactory keyFactory = null;

  public static PublicKey buildPublicKey(String publicKeyBase64String) throws Exception {
    X509EncodedKeySpec ukeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyBase64String));
    return getKeyFactory().generatePublic(ukeySpec);
  }

  public static PrivateKey buildPrivateKey(String privateKeyBase64String) throws Exception {
    PKCS8EncodedKeySpec rkeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyBase64String));
    return getKeyFactory().generatePrivate(rkeySpec);
  }

  private static KeyFactory getKeyFactory() throws Exception {
    if (keyFactory == null) {
      keyFactory = KeyFactory.getInstance("RSA");
    }

    return keyFactory;
  }
}
