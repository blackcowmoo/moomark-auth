package com.blackcowmoo.moomark.auth.config;

import java.util.Collections;

import javax.servlet.http.HttpSession;

import com.blackcowmoo.moomark.auth.model.AuthProvider;
import com.blackcowmoo.moomark.auth.model.dto.SessionUser;
import com.blackcowmoo.moomark.auth.model.entity.User;
import com.blackcowmoo.moomark.auth.service.OAuhUserServiceImpl;

import org.springframework.context.annotation.Profile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Profile("production")
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
  private final OAuhUserServiceImpl userService;
  private final HttpSession httpSession;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

    OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
    OAuth2User oAuth2User = delegate.loadUser(userRequest);

    String registrationId = userRequest.getClientRegistration().getRegistrationId();
    AuthProvider provider = AuthProvider.getAuthProviderValue(userRequest.getClientRegistration().getClientName());
    String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint()
        .getUserNameAttributeName();

    OAuthAttributes attributes = OAuthAttributes.of(provider, registrationId, userNameAttributeName,
        oAuth2User.getAttributes());
    User user = saveOrUpdate(attributes);
    httpSession.setAttribute("user", new SessionUser(user));

    return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey())),
        attributes.getAttributes(), attributes.getNameAttributeKey());
  }

  private User saveOrUpdate(OAuthAttributes attributes) {
    return userService.loginOrSignUp(attributes.toEntity());
  }
}
