package com.blackcowmoo.moomark.auth.util;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class HashUtilsTest {

  @Test
  void Sha256Test() {
    String testMsg = "TEST MESSAGE";
    String testMsg2 = "TEST MESSAGE2";
    String result = HashUtils.toSha256(testMsg);
    String result2 = HashUtils.toSha256(testMsg2);

    Assertions.assertThat(result).isNotEqualTo(result2);
  }
}
