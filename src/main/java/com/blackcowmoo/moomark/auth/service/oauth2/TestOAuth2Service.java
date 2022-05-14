package com.blackcowmoo.moomark.auth.service.oauth2;

import com.blackcowmoo.moomark.auth.model.AuthProvider;
import com.blackcowmoo.moomark.auth.model.entity.User;
import com.blackcowmoo.moomark.auth.model.oauth2.Token;
import com.blackcowmoo.moomark.auth.service.TokenService;
import com.blackcowmoo.moomark.auth.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TestOAuth2Service {

  @Value("${environment}")
  private String environment;

  @Autowired
  private UserService userService;

  @Autowired
  private TokenService tokenService;

  public Boolean isTest(String code) {
    return environment.equals("dev") && code.startsWith("test-");
  }

  public Token login(String testCode) {
    String[] tokenStrings = testCode.split("-");
    String id = tokenStrings[1];
    User user = userService.getUserById(id, AuthProvider.TEST);
    if (user == null) {
      user = userService.signUp(id, AuthProvider.TEST, "test", "test@blackcowmoo.com",
          "https://www.gravatar.com/avatar/HASH");
    }

    return tokenService.generateToken(user.getId(), user.getAuthProvider(), user.getRole());
  }
}
