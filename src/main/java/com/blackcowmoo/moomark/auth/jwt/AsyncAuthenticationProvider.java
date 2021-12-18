package com.blackcowmoo.moomark.auth.jwt;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import com.blackcowmoo.moomark.auth.exception.JpaException;
import com.blackcowmoo.moomark.auth.model.entity.User;
import com.blackcowmoo.moomark.auth.service.NormalUserServiceImpl;

public class AsyncAuthenticationProvider implements AuthenticationProvider {
  private NormalUserServiceImpl normalUserServiceImpl;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    if (authentication == null) {
      throw new IllegalArgumentException("Error issuing authentication");
    }

    String email = (String) authentication.getPrincipal();
    
    User user = null;
    try {
      user = normalUserServiceImpl.findByEmail(email);
    } catch (JpaException e) {
      e.printStackTrace();
    }

    return new UsernamePasswordAuthenticationToken(email, user.getRole());
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
  }

}
