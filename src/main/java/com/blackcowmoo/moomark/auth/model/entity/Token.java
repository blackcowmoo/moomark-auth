package com.blackcowmoo.moomark.auth.model.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Id;
import org.springframework.data.redis.core.RedisHash;
import lombok.Builder;
import lombok.Getter;


@Getter
@RedisHash("token")
public class Token implements Serializable {

  @Id
  private String id;
  private String userId;
  private LocalDateTime issueTime;

  @Builder
  public Token(String id, String userId, LocalDateTime issueTime) {
    this.id = id;
    this.userId = userId;
    this.issueTime = issueTime;
  }

  public void refreshToken(String userId, LocalDateTime refreshTime) {
    if (refreshTime.isAfter(this.issueTime)) {
      this.userId = userId;
      this.issueTime = refreshTime;
    }
  }
}
