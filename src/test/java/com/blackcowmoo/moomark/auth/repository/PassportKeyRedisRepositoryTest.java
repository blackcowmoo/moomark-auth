package com.blackcowmoo.moomark.auth.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.blackcowmoo.moomark.auth.model.entity.PassportKey;
import com.blackcowmoo.moomark.auth.util.AesUtil;
import com.blackcowmoo.moomark.auth.util.HashUtils;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;

@DataRedisTest
class PassportKeyRedisRepositoryTest {

  @Autowired
  PassportKeyRedisRepository passportKeyRedisRepository;

  @Autowired
  AesUtil aesUtil;

  @DisplayName("save test")
  @Test
  void save() throws Exception {
    //given
    String accessToken = "accessToken";
    String username = "username";
    SecretKey key = aesUtil.generateNewKey();
    String hash = HashUtils.toSha256(accessToken + username);
    PassportKey passportKey = PassportKey.builder()
      .key(key)
      .hash(hash)
      .build();

    //when
    passportKeyRedisRepository.save(passportKey);

    //then
    PassportKey findPassportKey = passportKeyRedisRepository.findById(accessToken)
      .orElseGet(null);

    assertAll(
      () -> assertEquals(key, findPassportKey.getKey()),
      () -> assertEquals(hash, findPassportKey.getHash())
    );
  }

}
