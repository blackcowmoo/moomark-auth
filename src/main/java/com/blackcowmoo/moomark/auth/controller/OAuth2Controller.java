package com.blackcowmoo.moomark.auth.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.blackcowmoo.moomark.auth.model.AuthProvider;
import com.blackcowmoo.moomark.auth.model.Role;
import com.blackcowmoo.moomark.auth.model.oauth2.GoogleTokenResult;
import com.blackcowmoo.moomark.auth.model.oauth2.Token;
import com.blackcowmoo.moomark.auth.service.TokenService;
import com.blackcowmoo.moomark.auth.service.oauth2.GoogleOAuth2Service;
import com.blackcowmoo.moomark.auth.service.oauth2.TestOAuth2Service;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class OAuth2Controller {
  private final TokenService tokenService;
  private final GoogleOAuth2Service googleOAuth2Service;
  private final TestOAuth2Service testOAuth2Service;

  @GetMapping("/api/v1/token/expired")
  public String auth() {
    throw new RuntimeException();
  }

  @GetMapping("/api/v1/token/refresh")
  public String refreshAuth(HttpServletRequest request, HttpServletResponse response) {
    String token = request.getHeader("Refresh");

    if (token != null && tokenService.verifyToken(token)) {
      String email = tokenService.getUid(token);
      Token newToken = tokenService.generateToken(email, AuthProvider.GOOGLE, Role.USER);

      response.addHeader("Auth", newToken.getToken());
      response.addHeader("Refresh", newToken.getRefreshToken());
      response.setContentType("application/json;charset=UTF-8");

      return "HAPPY NEW TOKEN";
    }

    throw new RuntimeException();
  }

  @GetMapping("/api/v1/oauth2/google")
  public Token googleCode(HttpServletRequest request, HttpServletResponse response) {
    String code = request.getParameter("code");

    if (testOAuth2Service.isTest(code)) {
      return testOAuth2Service.login(code);
    }

    String token = googleOAuth2Service.getToken(code);
    GoogleTokenResult googleUserInfo = googleOAuth2Service.parseIdToken(token);

    return googleOAuth2Service.login(googleUserInfo);
  }
}
