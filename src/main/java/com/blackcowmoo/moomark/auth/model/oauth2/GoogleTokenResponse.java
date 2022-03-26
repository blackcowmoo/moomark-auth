package com.blackcowmoo.moomark.auth.model.oauth2;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@Getter
public class GoogleTokenResponse {
  @JsonProperty("access_token")
  private String accessToken;
  @JsonProperty("expires_in")
  private Integer expiresIn;
  private String scope;
  @JsonProperty("token_type")
  private String tokenType;
  @JsonProperty("id_token")
  private String idToken;

  public Boolean isExpired() {
    return this.expiresIn <= 0;
  }
}
