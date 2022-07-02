package com.blackcowmoo.moomark.auth.configuration;

import com.blackcowmoo.moomark.auth.service.PassportService;
import com.blackcowmoo.moomark.auth.service.TokenService;
import com.blackcowmoo.moomark.auth.service.UserService;
import com.blackcowmoo.moomark.auth.configuration.oauth2.JwtAuthFilter;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
  private final TokenService tokenService;
  private final UserService userService;
  private final PassportService passportService;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and().csrf().disable().formLogin().disable().httpBasic().disable().logout().disable()
        .exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
        .and().authorizeRequests()
        .antMatchers("/api/v1/oauth2/refresh").permitAll()
        .antMatchers("/api/v1/oauth2/google").permitAll()
        .antMatchers("/api/v1/user/{provider}/{userId}").permitAll()
        .antMatchers("/api/v1/passport/verify").permitAll()
        .antMatchers("/api/v1/passport/verify/public").permitAll()
        .antMatchers("/actuator/health").permitAll()
        .anyRequest().authenticated();

    http.addFilterBefore(new JwtAuthFilter(passportService, tokenService, userService),
        UsernamePasswordAuthenticationFilter.class);
  }
}
