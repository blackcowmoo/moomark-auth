package com.blackcowmoo.moomark.auth.service.oauth2;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import com.blackcowmoo.moomark.auth.model.AuthProvider;
import com.blackcowmoo.moomark.auth.model.entity.User;
import com.blackcowmoo.moomark.auth.model.oauth2.GoogleTokenResponse;
import com.blackcowmoo.moomark.auth.model.oauth2.GoogleTokenResult;
import com.blackcowmoo.moomark.auth.model.oauth2.Token;
import com.blackcowmoo.moomark.auth.service.TokenService;
import com.blackcowmoo.moomark.auth.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GoogleOAuth2Service {
  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private ObjectMapper mapper;

  @Autowired
  private UserService userService;

  @Autowired
  private TokenService tokenService;

  @Value("${spring.security.oauth2.client.registration.google.client-id}")
  private String clientId;
  @Value("${spring.security.oauth2.client.registration.google.client-secret}")
  private String clientSecret;
  @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
  private String redirectUri;
  @Value("${spring.security.oauth2.client.registration.google.authorization-grant-type}")
  private String grantType;

  public String getToken(String code) {
    Map<String, String> parameters = new HashMap<>();
    parameters.put("client_id", clientId);
    parameters.put("client_secret", clientSecret);
    parameters.put("redirect_uri", redirectUri);
    parameters.put("grant_type", grantType);
    parameters.put("code", code);

    GoogleTokenResponse result = restTemplate.postForObject("https://oauth2.googleapis.com/token", parameters,
        GoogleTokenResponse.class);

    if (result.isExpired()) {
      throw new RuntimeException("ExpiredGoogleCode");
    }

    return result.getIdToken();
  }

  public GoogleTokenResult parseIdToken(String idToken) {
    String body = new String(Base64.getDecoder().decode(idToken.split("\\.")[1]));
    try {
      return mapper.readValue(body, GoogleTokenResult.class);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      return null;
    }
  }

  public Token login(GoogleTokenResult googleUser) {
    User user = userService.getUserById(googleUser.getSub(), AuthProvider.GOOGLE);
    if (user == null) {
      user = userService.signUp(googleUser.getSub(), AuthProvider.GOOGLE, googleUser.getName(), googleUser.getEmail(),
          googleUser.getPicture());
    }

    return tokenService.generateToken(user.getId(), user.getAuthProvider(), user.getRole());
  }
}
