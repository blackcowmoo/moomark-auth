package com.blackcowmoo.moomark.auth.service.oauth2;

import com.blackcowmoo.moomark.auth.model.AuthProvider;
import com.blackcowmoo.moomark.auth.model.entity.User;
import com.blackcowmoo.moomark.auth.model.oauth2.Token;
import com.blackcowmoo.moomark.auth.service.TokenService;
import com.blackcowmoo.moomark.auth.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestOAuth2Service {
  @Autowired
  private UserService userService;

  @Autowired
  private TokenService tokenService;

  public Token login(String testCode) {
    String[] tokenStrings = testCode.split("-");
    String id = tokenStrings[1];
    User user = userService.getUserById(id, AuthProvider.TEST);
    if (user == null) {
      user = userService.signUp(id, AuthProvider.GOOGLE, "test", "test@blackcowmoo.com",
          "https://www.gravatar.com/avatar/HASH");
    }

    return tokenService.generateToken(user.getId(), user.getAuthProvider(), user.getRole());
  }
}
