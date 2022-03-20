package com.blackcowmoo.moomark.auth.configuration.oauth2;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.blackcowmoo.moomark.auth.TokenService;
import com.blackcowmoo.moomark.auth.model.Token;
import com.blackcowmoo.moomark.auth.model.UserRequestMapper;
import com.blackcowmoo.moomark.auth.model.dto.UserDto;
import com.blackcowmoo.moomark.auth.model.entity.User;
import com.blackcowmoo.moomark.auth.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
  private final TokenService tokenService;
  private final UserRequestMapper userRequestMapper;
  private final UserService userService;
  private final ObjectMapper objectMapper;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication)
      throws IOException, ServletException {

    OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
    UserDto userDto = userRequestMapper.toDto(oAuth2User);

    User user = userService.getUserById(userDto.getId(), userDto.getProvider());
    if (user == null) {
      user = userService.signUp(userDto.getId(), userDto.getProvider(), userDto.getName(), userDto.getEmail(),
          userDto.getPicture());
    }

    Token token = tokenService.generateToken(user.getId(), user.getAuthProvider(), user.getRole());

    writeTokenResponse(response, token);
  }

  private void writeTokenResponse(HttpServletResponse response, Token token)
      throws IOException {
    response.setContentType("text/html;charset=UTF-8");

    response.addHeader("Auth", token.getToken());
    response.addHeader("Refresh", token.getRefreshToken());
    response.setContentType("application/json;charset=UTF-8");

    var writer = response.getWriter();
    writer.println(objectMapper.writeValueAsString(token));
    writer.flush();
  }
}
