package com.blackcowmoo.moomark.auth.repository;


import com.blackcowmoo.moomark.auth.model.entity.PassportKey;
import com.blackcowmoo.moomark.auth.util.AesUtil;
import com.blackcowmoo.moomark.auth.util.HashUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@DisplayName("Redis CRUD Test")
@SpringBootTest(
  webEnvironment = WebEnvironment.RANDOM_PORT
)
class PassportKeyRedisRepositoryTest {
  @Autowired
  private PassportKeyRedisRepository passportKeyRedisRepository;
  @Autowired
  private AesUtil aesUtil;

  private final String TEST_PRIFIX = "TEST";
  private PassportKey passportKey;
  @BeforeEach
  void initTest() {
    passportKey = PassportKey.builder()
      .key(aesUtil.generateNewKey())
      .hash(makeKeyHash(TEST_PRIFIX, "TEST_USER"))
      .build();
  }

  @AfterEach
  void tearDown() {
    passportKeyRedisRepository.deleteById(passportKey.getHash());
  }

  @Test
  void saveTest() {
    // given
    passportKeyRedisRepository.save(passportKey);

    // when
    PassportKey psersistPassportKey = passportKeyRedisRepository
      .findById(passportKey.getHash())
      .orElseThrow(RuntimeException::new);


    // then
    Assertions.assertThat(psersistPassportKey.getHash()).isEqualTo(passportKey.getHash());
  }

  private String makeKeyHash(String providerValue, String id) {
    return HashUtils.toSha256(providerValue + id);
  }
}
