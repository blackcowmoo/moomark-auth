package com.blackcowmoo.moomark.auth.controller;

import javax.servlet.http.HttpServletResponse;

import com.blackcowmoo.moomark.auth.model.dto.TokenResponse;
import com.blackcowmoo.moomark.auth.model.oauth2.GoogleTokenResult;
import com.blackcowmoo.moomark.auth.model.oauth2.Token;
import com.blackcowmoo.moomark.auth.service.TokenService;
import com.blackcowmoo.moomark.auth.service.oauth2.GoogleOAuth2Service;
import com.blackcowmoo.moomark.auth.service.oauth2.TestOAuth2Service;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/oauth2")
public class OAuth2Controller {
  private static class RefreshTokenRequestBody {
    public String refreshToken;
  }

  private final TokenService tokenService;
  private final GoogleOAuth2Service googleOAuth2Service;
  private final TestOAuth2Service testOAuth2Service;

  @GetMapping("/google")
  public Token googleCode(@RequestParam String code) {
    if (testOAuth2Service.isTest(code)) {
      return testOAuth2Service.login(code);
    }

    String token = googleOAuth2Service.getToken(code);
    GoogleTokenResult googleUserInfo = googleOAuth2Service.parseIdToken(token);

    return googleOAuth2Service.login(googleUserInfo);
  }

  @PostMapping("/refresh")
  public Token refreshToken(@RequestBody RefreshTokenRequestBody body, HttpServletResponse response) {
    TokenResponse tokenResponse = tokenService.verifyRefreshToken(body.refreshToken);

    if (tokenResponse != null) {
      return tokenService.generateToken(tokenResponse.getId(), tokenResponse.getProvider(), tokenResponse.getRole());
    }

    response.setStatus(401);
    return null;
  }
}
