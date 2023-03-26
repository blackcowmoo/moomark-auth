package com.blackcowmoo.moomark.auth.repository;


import com.blackcowmoo.moomark.auth.model.entity.PassportKey;
import com.blackcowmoo.moomark.auth.util.AesUtil;
import com.blackcowmoo.moomark.auth.util.HashUtils;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Slf4j
@Testcontainers
@DataRedisTest
class PassportKeyRedisRepositoryTest {

  @Autowired
  private PassportKeyRedisRepository passportKeyRedisRepository;
  private final AesUtil aesUtil = new AesUtil();
  private static final String DOCKER_IMG_NAME = "redis:6-alpine";

  @Container
  private static final GenericContainer<?> REDIS_CONTAINER = new GenericContainer<>(DockerImageName.parse(DOCKER_IMG_NAME))
    .withExposedPorts(6379)
    .withReuse(true);

  @DynamicPropertySource
  public static void properties(DynamicPropertyRegistry registry) {
    registry.add("spring.redis.host", REDIS_CONTAINER::getHost);
    registry.add("spring.redis.port", () -> "" + REDIS_CONTAINER.getMappedPort(6379));

  }

  @Test
  void saveKeyTest() {
    PassportKey passportKey = PassportKey.builder()
      .key(aesUtil.generateNewKey())
      .hash(HashUtils.toSha256("TEST"))
      .build();

    System.out.println("Passport key : " + Arrays.toString(passportKey.getKey().getEncoded()));
    System.out.println("Passport key : " + passportKey.getHash());
    PassportKey savedPassportKey = passportKeyRedisRepository.save(passportKey);

    Assertions.assertThat(passportKey.getHash()).isEqualTo(savedPassportKey.getHash());
  }
}
