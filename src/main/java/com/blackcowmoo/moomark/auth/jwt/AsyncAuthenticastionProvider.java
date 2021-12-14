package com.blackcowmoo.moomark.auth.jwt;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import com.blackcowmoo.moomark.auth.exception.JpaException;
import com.blackcowmoo.moomark.auth.model.entity.User;
import com.blackcowmoo.moomark.auth.service.NormalUserServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AsyncAuthenticastionProvider implements AuthenticationProvider {
  private final NormalUserServiceImpl normalUserServiceImpl;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    if (authentication == null) {
      throw new IllegalArgumentException("authentication 발급 오류");
    }

    String email = (String) authentication.getPrincipal();
    User user = null;
    try {
      user = normalUserServiceImpl.loadUserByEmail(email);
      log.info("issue token to {}", user.getEmail());
    } catch (JpaException e) {
      e.printStackTrace();
      throw new BadCredentialsException("Cannot find user information");
    }
    return new UsernamePasswordAuthenticationToken(user.getEmail(), user.getRole().getTitle());
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
  }

}
