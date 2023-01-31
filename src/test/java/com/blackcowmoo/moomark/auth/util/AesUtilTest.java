package com.blackcowmoo.moomark.auth.util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class AesUtilTest {


  @Test
  void encryptAndDecryptTest()
      throws IllegalBlockSizeException, NoSuchPaddingException,
      NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
    String plainText = "PLAIN TEXT";

    SecretKey aesSecretKey = AesUtil.generateNewKey();
    byte[] encryptData = AesUtil.encrypt(plainText, aesSecretKey);
    String decryptionText = AesUtil.decrypt(encryptData, aesSecretKey);

    Assertions.assertThat(plainText).isEqualTo(decryptionText);
  }
}
