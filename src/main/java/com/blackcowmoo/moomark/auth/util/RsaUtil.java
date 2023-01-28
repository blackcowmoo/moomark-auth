package com.blackcowmoo.moomark.auth.util;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;

import javax.crypto.NoSuchPaddingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
public class RsaUtil {

  private static final String ALGO_TYPE = "RSA";
  private static final String ALGO_TYPE_PADDING = "RSA/ECB/PKCS1Padding";
  private static KeyFactory keyFactory;
  private static Cipher cipher;

  private static PublicKey publicKey;

  private static PrivateKey privateKey;
  @Value("${RSA_PUBLIC_KEY}")
  private static String publicKeyString;

  @Value("${passport.private-key}")
  private static String privateKeyString;

  private RsaUtil() throws InstantiationException {
    throw new InstantiationException("Can not instantiation class");
  }

  @PostConstruct
  private static void init() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException {
    keyFactory = KeyFactory.getInstance(ALGO_TYPE);
    cipher = Cipher.getInstance(ALGO_TYPE_PADDING);
    publicKey = buildPublicKey(publicKeyString);
    privateKey = buildPrivateKey(privateKeyString);
  }

  public static String getPublicKey() {
    return publicKeyString;
  }


  public static PublicKey buildPublicKey(String publicKeyBase64String) throws InvalidKeySpecException {
    X509EncodedKeySpec ukeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyBase64String));
    return keyFactory.generatePublic(ukeySpec);
  }

  public static PrivateKey buildPrivateKey(String privateKeyBase64String) throws InvalidKeySpecException {
    PKCS8EncodedKeySpec rkeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyBase64String));
    return keyFactory.generatePrivate(rkeySpec);
  }

  public static byte[] encryptByPublicKey(String data) {
    try {
      cipher.init(Cipher.ENCRYPT_MODE, publicKey);
      return cipher.doFinal(data.getBytes());
    } catch (Exception e) {
      log.error("encryptByPublicKey: ", e);
      return null;
    }
  }

  public static String decryptByPublicKey(byte[] data) {
    try {
      cipher.init(Cipher.DECRYPT_MODE, publicKey);
      return new String(cipher.doFinal(data));
    } catch (Exception e) {
      log.error("decryptByPublicKey: ", e);
      return null;
    }
  }

  public static byte[] encryptByPrivateKey(String data) {
    return encryptByPrivateKey(data.getBytes());
  }

  public static byte[] encryptByPrivateKey(byte[] data) {
    try {
      cipher.init(Cipher.ENCRYPT_MODE, privateKey);
      return cipher.doFinal(data);
    } catch (Exception e) {
      log.error("encryptByPrivateKey: ", e);
      return null;
    }
  }

  public static String decryptByPrivateKey(byte[] data) {
    try {
      cipher.init(Cipher.DECRYPT_MODE, privateKey);
      return new String(cipher.doFinal(data));
    } catch (Exception e) {
      log.error("decryptByPrivateKey: ", e);
      return null;
    }
  }
}
