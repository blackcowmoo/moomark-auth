package com.blackcowmoo.moomark.auth.model.entity;

import java.io.Serializable;
import javax.persistence.Column;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash(value = "Passport", timeToLive = 1800)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PassportKey implements Serializable {

  @Id
  @Column(name = "hash")
  private String hash;
  private byte[] key;

  @Builder
  public PassportKey(byte[] key, String hash) {
    this.key = key;
    this.hash = hash;
  }
}
