package com.blackcowmoo.moomark.auth.configuration;

import com.blackcowmoo.moomark.auth.configuration.oauth2.OAuth2UserServiceCustom;
import com.blackcowmoo.moomark.auth.service.UserService;
import com.blackcowmoo.moomark.auth.TokenService;
import com.blackcowmoo.moomark.auth.configuration.oauth2.JwtAuthFilter;
import com.blackcowmoo.moomark.auth.configuration.oauth2.OAuth2SuccessHandler;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
  private final OAuth2UserServiceCustom oauth2UserService;
  private final OAuth2SuccessHandler successHandler;
  private final TokenService tokenService;
  private final UserService userService;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.httpBasic().disable()
        .csrf().disable()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and().authorizeRequests().anyRequest().authenticated()
        .and().oauth2Login().successHandler(successHandler)
        .userInfoEndpoint().userService(oauth2UserService);

    http.addFilterBefore(new JwtAuthFilter(tokenService, userService), UsernamePasswordAuthenticationFilter.class);
  }
}
