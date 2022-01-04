package com.blackcowmoo.moomark.auth.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class JwtTokenProviderTest {

  @Autowired
  private JwtTokenProvider jwtTokenProvider;

  @Test
  @DisplayName("test jwt refresh token test")
  public void testCreateRefreshToken() {
    String accessToken = jwtTokenProvider.createJwtRefreshToken("REFRESH_TOKEN");
    String givenValue = String.valueOf(jwtTokenProvider.getClaimsFromJwtToken(accessToken).getBody().get("value"));
    if (!givenValue.equals("REFRESH_TOKEN")) {
      fail("refresh token is error");
    }
    assertEquals("REFRESH_TOKEN", givenValue);
  }
}
