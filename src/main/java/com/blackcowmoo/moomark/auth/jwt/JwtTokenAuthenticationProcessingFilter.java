package com.blackcowmoo.moomark.auth.jwt;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;
import com.blackcowmoo.moomark.auth.config.SecurityConfig;
import com.blackcowmoo.moomark.auth.util.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

public class JwtTokenAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {
  private final AuthenticationFailureHandler failureHandler;


  public JwtTokenAuthenticationProcessingFilter(RequestMatcher matcher,
      AuthenticationFailureHandler failureHandler) {
    super(matcher);
    this.failureHandler = failureHandler;
  }

  /**
   * 요청 Header에서 JWT토큰을 획득하여 JwtAuthenticationToken 토큰을 생성함.
   * 
   * @param request
   * @param response
   * @return
   * @throws AuthenticationException
   * @throws IOException
   * @throws ServletException
   */
  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response) throws AuthenticationException {
    String tokenPayload = request.getHeader(SecurityConfig.AUTHENTICATION_HEADER_NAME);
    Jws<Claims> claimsJws = JwtUtils.parseToken(tokenPayload);

    return getAuthenticationManager().authenticate(new JwtAuthenticationToken(claimsJws));
  }

  /**
   * 인증(Authentication) 성공 시 실행
   * 
   * @param request
   * @param response
   * @param chain
   * @param authResult
   * @throws IOException
   * @throws ServletException
   */
  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain, Authentication authResult) throws IOException, ServletException {
    SecurityContext context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(authResult);
    SecurityContextHolder.setContext(context);
    chain.doFilter(request, response);
  }

  /**
   * 인증(Authentication) 실패 시 실행
   * 
   * @param request
   * @param response
   * @param failed
   * @throws IOException
   * @throws ServletException
   */
  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request,
      HttpServletResponse response, AuthenticationException failed)
      throws IOException, ServletException {
    SecurityContextHolder.clearContext();

    // FailureHandler에 처리 로직 위임
    failureHandler.onAuthenticationFailure(request, response, failed);
  }
}
