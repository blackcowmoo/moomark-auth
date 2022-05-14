package com.blackcowmoo.moomark.auth.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum AuthProvider {

  TEST("TEST", "테스트"), GOOGLE("GOOGLE", "구글"), GITHUB("GITHUB", "깃허브"),
  // KAKAO("KAKAO", "카카오"), NAVER("NAVER", "네이버"),
  EMPTY("EMPTY", "없음");

  private final String key;
  private final String value;

  public static AuthProvider getAuthProviderValue(String provider) {
    return Arrays.stream(AuthProvider.values()).filter(authProvider -> authProvider.hasAuthProvider(provider)).findAny()
        .orElse(EMPTY);
  }

  public boolean hasAuthProvider(String provider) {
    return key.equals(provider);
  }
}
