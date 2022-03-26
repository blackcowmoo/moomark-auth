package com.blackcowmoo.moomark.auth.model.oauth2;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@Getter
public class GoogleTokenResult {
  private String iss;
  private String azp;
  private String aud;
  private String sub;
  private String email;
  @JsonProperty("email_verified")
  private Boolean emailVerified;
  @JsonProperty("at_hash")
  private String atHash;
  private String name;
  private String picture;
  @JsonProperty("given_name")
  private String givenName;
  @JsonProperty("family_name")
  private String familyName;
  private String locale;
  private Integer iat;
  private Integer exp;
}
