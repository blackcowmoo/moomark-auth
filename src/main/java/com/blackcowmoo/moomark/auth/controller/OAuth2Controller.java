package com.blackcowmoo.moomark.auth.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.blackcowmoo.moomark.auth.TokenService;
import com.blackcowmoo.moomark.auth.model.AuthProvider;
import com.blackcowmoo.moomark.auth.model.Role;
import com.blackcowmoo.moomark.auth.model.Token;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class OAuth2Controller {
  private final TokenService tokenService;

  @GetMapping("/token/expired")
  public String auth() {
    throw new RuntimeException();
  }

  @GetMapping("/token/refresh")
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
}
