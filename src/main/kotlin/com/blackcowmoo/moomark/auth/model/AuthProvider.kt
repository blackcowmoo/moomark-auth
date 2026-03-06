package com.blackcowmoo.moomark.auth.model

import java.util.Arrays

enum class AuthProvider(val key: String, val value: String) {
  TEST("TEST", "테스트"),
  GOOGLE("GOOGLE", "구글"),
  GITHUB("GITHUB", "깃허브"),
  EMPTY("EMPTY", "없음");

  companion object {
    @JvmStatic
    fun getAuthProviderValue(provider: String): AuthProvider {
      return Arrays.stream(AuthProvider.values())
        .filter { it.hasAuthProvider(provider) }
        .findAny()
        .orElse(EMPTY)
    }
  }

  fun hasAuthProvider(provider: String): Boolean {
    return key == provider
  }
}
