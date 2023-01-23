package com.blackcowmoo.moomark.auth.util;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RsaUtil {
  private KeyFactory keyFactory;
  private Cipher cipher;

  private PublicKey publicKey;
  private PrivateKey privateKey;

  public RsaUtil(String publicKeyBase64String, String privateKeyBase64String) throws Exception {
    keyFactory = KeyFactory.getInstance("RSA");
    cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
    publicKey = buildPublicKey(publicKeyBase64String);
    privateKey = buildPrivateKey(privateKeyBase64String);
  }

  public PublicKey buildPublicKey(String publicKeyBase64String) throws Exception {
    X509EncodedKeySpec ukeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyBase64String));
    return keyFactory.generatePublic(ukeySpec);
  }

  public PrivateKey buildPrivateKey(String privateKeyBase64String) throws Exception {
    PKCS8EncodedKeySpec rkeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyBase64String));
    return keyFactory.generatePrivate(rkeySpec);
  }

  public byte[] encryptByPublicKey(String data) {
    try {
      cipher.init(Cipher.ENCRYPT_MODE, publicKey);
      return cipher.doFinal(data.getBytes());
    } catch (Exception e) {
      log.error("encryptByPublicKey: ", e);
      return null;
    }
  }

  public String decryptByPublicKey(byte[] data) {
    try {
      cipher.init(Cipher.DECRYPT_MODE, publicKey);
      return new String(cipher.doFinal(data));
    } catch (Exception e) {
      log.error("decryptByPublicKey: ", e);
      return null;
    }
  }

  public byte[] encryptByPrivateKey(String data) {
    return encryptByPrivateKey(data.getBytes());
  }

  public byte[] encryptByPrivateKey(byte[] data) {
    try {
      cipher.init(Cipher.ENCRYPT_MODE, privateKey);
      return cipher.doFinal(data);
    } catch (Exception e) {
      log.error("encryptByPrivateKey: ", e);
      return null;
    }
  }

  public String decryptByPrivateKey(byte[] data) {
    try {
      cipher.init(Cipher.DECRYPT_MODE, privateKey);
      return new String(cipher.doFinal(data));
    } catch (Exception e) {
      log.error("decryptByPrivateKey: ", e);
      return null;
    }
  }
}
