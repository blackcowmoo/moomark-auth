package com.blackcowmoo.moomark.auth.jwt;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import com.blackcowmoo.moomark.auth.model.dto.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AsyncLoginProcessFilter extends AbstractAuthenticationProcessingFilter {
  private final ObjectMapper objectMapper;
  private final AuthenticationSuccessHandler authenticationSuccessHandler;
  private final AuthenticationFailureHandler authenticationFailureHandler;

  protected AsyncLoginProcessFilter(String defaultFilterProcessesUrl, ObjectMapper objectMapper,
      AuthenticationFailureHandler authenticationFailureHandler,
      AuthenticationSuccessHandler authenticationSuccessHandler) {
    super(defaultFilterProcessesUrl);
    this.objectMapper = objectMapper;
    this.authenticationFailureHandler = authenticationFailureHandler;
    this.authenticationSuccessHandler = authenticationSuccessHandler;
  }

  /**
   * 비동기 post형식으로 온 요청에 대해 username, password를 받아 토큰 생성 후 AuthenticationManager에게 전달함.
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
      HttpServletResponse response)
      throws AuthenticationServiceException, IOException, ServletException {
    if (this.isNotPostMethod(request) || this.isNotAsync(request)) {
      log.debug("비동기 로그인 처리 지원이 되지 않는 메소드 요청입니다. :: " + request.getMethod());
      throw new com.blackcowmoo.moomark.auth.exception.AuthenticationNotSupportedException(
          "Authentication method is not supported");
    }
    LoginRequest loginRequest = objectMapper.readValue(request.getReader(), LoginRequest.class);
    UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
        loginRequest.getUsername(), loginRequest.getPassword());
    return this.getAuthenticationManager().authenticate(token);
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain, Authentication authResult) throws IOException, ServletException {

    authenticationSuccessHandler.onAuthenticationSuccess(request, response, authResult);
  }

  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request,
      HttpServletResponse response, AuthenticationException failed)
      throws IOException, ServletException {
    // 실패 시 처리 로직을 FailureHandler에 위임함.
    authenticationFailureHandler.onAuthenticationFailure(request, response, failed);
  }

  /**
   * 비동기 처리 요청인지 확인.
   * 
   * @param request
   * @return
   */
  private boolean isNotAsync(HttpServletRequest request) {
    return !"XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
  }

  /**
   * Post 요청 여부 확인.
   * 
   * @param request
   * @return
   */
  private boolean isNotPostMethod(HttpServletRequest request) {
    return !HttpMethod.POST.name().equals(request.getMethod());
  }
}
