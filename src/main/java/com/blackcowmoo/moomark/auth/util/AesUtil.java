package com.blackcowmoo.moomark.auth.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Component
@Slf4j
public class AesUtil {
  private final String CIPHER = "AES";

  private SecureRandom secureRandom = new SecureRandom();

  public byte[] encrypt(String body, SecretKey key) throws Exception {
    Cipher cipher = Cipher.getInstance("AES");
    cipher.init(Cipher.ENCRYPT_MODE, key);
    byte[] encrypted = cipher.doFinal(body.getBytes());
    return encrypted;
  }

  public String decrypt(byte[] body, SecretKey key) throws Exception {
    Cipher cipher = Cipher.getInstance("AES");
    cipher.init(Cipher.DECRYPT_MODE, key);
    byte[] decrypted = cipher.doFinal(body);
    return new String(decrypted);
  }

  public SecretKey generateNewKey() {
    SecretKey key = getRandomKey(CIPHER, 128);
    if (key == null) {
      return null;
    }
    return key;
  }

  private SecretKey getRandomKey(String cipher, int keySize) {
    try {
      KeyGenerator generator = KeyGenerator.getInstance("AES");
      SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
      random.setSeed(getRandomBytes(keySize));
      generator.init(128, random);
      return generator.generateKey();
    } catch (NoSuchAlgorithmException e) {
      log.error(e.getMessage(), e);
      return null;
    }
  }

  private byte[] getRandomBytes(int keySize) {
    byte[] secureRandomKeyBytes = new byte[keySize / 8];
    secureRandom.nextBytes(secureRandomKeyBytes);
    return secureRandomKeyBytes;
  }
}
