package com.blackcowmoo.moomark.auth.config.redis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import redis.embedded.RedisServer;

@Slf4j
@Configuration
@Profile({"development", "local"})
public class EmbeddedRedisConfig {

  @Value("${spring.redis.port}")
  private int port;
  private RedisServer redisServer;

  @PostConstruct
  public void redisServer() throws IOException {
    // int redisPort = isRedisRunning() ? findAvailablePort(a) : port;
    redisServer = new RedisServer(port);
    log.debug("Redis server is start");
    log.debug("running port : {}", port);
    redisServer.start();
  }

  @PreDestroy
  public void stopRedis() {
    if (redisServer != null) {
      log.debug("Redis server is stop");
      redisServer.stop();
    }
  }

  private boolean isRunning(Process process) {
    String line;
    StringBuilder pidInfo = new StringBuilder();

    try (BufferedReader input =
        new BufferedReader(new InputStreamReader(process.getInputStream()))) {

      while ((line = input.readLine()) != null) {
        pidInfo.append(line);
      }

    } catch (Exception e) {
      e.printStackTrace();
    }

    return !StringUtils.isEmpty(pidInfo.toString());
  }

  private Process executeGrepProcessCommand(int port) throws IOException {
    String command = String.format("netstat -nat | grep LISTEN|grep %d", port);
    String[] shell = {"/bin/sh", "-c", command};
    return Runtime.getRuntime().exec(shell);
  }

  private boolean isRedisRunning() throws IOException {
    return isRunning(executeGrepProcessCommand(port));
  }

  public int findAvailablePort() throws IOException {
    for (int portNumber = 10000; portNumber <= 65535; portNumber++) {
      Process process = executeGrepProcessCommand(portNumber);
      if (!isRunning(process)) {
        return port;
      }
    }

    throw new IllegalArgumentException("Can not found available port : 10000 - 65535");
  }
}
