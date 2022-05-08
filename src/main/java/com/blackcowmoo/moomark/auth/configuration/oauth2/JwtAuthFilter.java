package com.blackcowmoo.moomark.auth.configuration.oauth2;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.blackcowmoo.moomark.auth.model.AuthProvider;
import com.blackcowmoo.moomark.auth.model.entity.User;
import com.blackcowmoo.moomark.auth.service.TokenService;
import com.blackcowmoo.moomark.auth.service.UserService;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthFilter extends GenericFilterBean {
  private final TokenService tokenService;
  private final UserService userService;

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    String token = ((HttpServletRequest) request).getHeader("Authorization");

    if (token != null && tokenService.verifyToken(token)) {

      String id = tokenService.getUid(token);
      AuthProvider provider = tokenService.getProvider(token);

      User user = userService.getUserById(id, provider);

      Authentication auth = getAuthentication(user);
      SecurityContextHolder.getContext().setAuthentication(auth);
    }

    chain.doFilter(request, response);
  }

  public Authentication getAuthentication(User user) {
    return new UsernamePasswordAuthenticationToken(user, "",
        Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
  }
}
