package com.blackcowmoo.moomark.auth.util;

import java.security.InvalidKeyException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@Component
@Slf4j
public class AesUtil {

  private AesUtil() throws InstantiationException {
    throw new InstantiationException("Can not instantiation class");
  }

  private static final String CIPHER = "AES";
  private static final Integer KEY_SIZE = 128;
  private static final SecureRandom secureRandom = new SecureRandom();

  public static byte[] encrypt(String body, SecretKey key)
    throws NoSuchAlgorithmException, InvalidKeyException,
    IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException {
    Cipher cipher = Cipher.getInstance(CIPHER);
    cipher.init(Cipher.ENCRYPT_MODE, key);
    return cipher.doFinal(body.getBytes());
  }

  public static String decrypt(byte[] body, SecretKey key)
    throws NoSuchAlgorithmException, InvalidKeyException,
    IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException {
    Cipher cipher = Cipher.getInstance(CIPHER);
    cipher.init(Cipher.DECRYPT_MODE, key);
    byte[] decrypted = cipher.doFinal(body);
    return new String(decrypted);
  }

  public static SecretKey generateNewKey() {
    return getRandomKey();
  }

  private static SecretKey getRandomKey() {
    KeyGenerator generator = null;
    try {
      generator = KeyGenerator.getInstance(AesUtil.CIPHER);
      SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
      random.setSeed(getRandomBytes());
      generator.init(KEY_SIZE, random);
      return generator.generateKey();
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException(e.getMessage(), e);
    }
  }

  private static byte[] getRandomBytes() {
    byte[] secureRandomKeyBytes = new byte[AesUtil.KEY_SIZE / 8];
    secureRandom.nextBytes(secureRandomKeyBytes);
    return secureRandomKeyBytes;
  }
}
