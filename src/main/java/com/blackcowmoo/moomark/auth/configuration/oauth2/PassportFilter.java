package com.blackcowmoo.moomark.auth.configuration.oauth2;

import com.blackcowmoo.moomark.auth.model.entity.User;
import com.blackcowmoo.moomark.auth.service.PassportService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;


@RequiredArgsConstructor
public class PassportFilter extends GenericFilterBean {
  private final PassportService passportService;

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
    throws IOException, ServletException {
    String passport = ((HttpServletRequest) request).getHeader("x-moom-passport-user");
    String key = ((HttpServletRequest) request).getHeader("x-moom-passport-key");

    if (passport != null && key != null) {
      User user = passportService.parsePassport(passport, key);
      Authentication auth = getAuthentication(user);
      SecurityContextHolder.getContext().setAuthentication(auth);
    }

    chain.doFilter(request, response);
  }

  private Authentication getAuthentication(User user) {
    return new UsernamePasswordAuthenticationToken(user, "",
      Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
  }
}
