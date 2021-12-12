package com.blackcowmoo.moomark.auth.config.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

// import com.blackcowmoo.moomark.auth.util.JwtUtil;

// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  // private final JwtUtil jwtUtil;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    ObjectMapper mapper = new ObjectMapper();
    try {
      String json = mapper.writeValueAsString(request);
      System.out.println(json);
      logger.info(json);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    // if (isAppropriateRequestForFilter(request)) {
    // try {
    // String token = jwtUtil.resolveToken(request);
    // Authentication authentication = jwtUtil.getAuthentication(token);
    // SecurityContextHolder.getContext().setAuthentication(authentication);
    // } catch (JWTVerificationException e) {
    // /* ... */
    // }
    // }
    filterChain.doFilter(request, response);
  }

  /* ... */
}
