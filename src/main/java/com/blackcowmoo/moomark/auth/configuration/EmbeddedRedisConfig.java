package com.blackcowmoo.moomark.auth.configuration;

import java.io.IOException;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import redis.embedded.RedisServer;

@Profile("test")
@Configuration
public class EmbeddedRedisConfig {

  private final RedisServer redisServer;

  public EmbeddedRedisConfig(@Value("${spring.redis.port}") int port) throws IOException {
    this.redisServer = new RedisServer(port);
  }

  @PostConstruct
  public void startRedis() {
    this.redisServer.start();
  }

  @PreDestroy
  public void stopRedis() {
    this.redisServer.stop();
  }
}
