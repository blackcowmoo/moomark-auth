package com.blackcowmoo.moomark.auth.configuration;

import java.io.IOException;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import redis.embedded.RedisServer;

@Slf4j
@Profile("local")
@Configuration
public class LocalRedisConfig {

  @Value("${spring.redis.port}")
  private int port;

  private RedisServer redisServer;

  @PostConstruct
  public void startRedisServer() throws IOException {
    redisServer = new RedisServer(port);
    redisServer.start();
  }

  @PreDestroy
  public void stopRedisServer() {
    if (!Objects.isNull(redisServer)) {
      redisServer.stop();
    }
  }

}
