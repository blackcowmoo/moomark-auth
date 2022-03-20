package com.blackcowmoo.moomark.auth.model;

import com.blackcowmoo.moomark.auth.model.dto.UserDto;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Component
public class UserRequestMapper {
  public UserDto toDto(OAuth2User oAuth2User) {
    var attributes = oAuth2User.getAttributes();
    return UserDto.builder()
        .id((String) attributes.get("id"))
        .email((String) attributes.get("email"))
        .name((String) attributes.get("name"))
        .picture((String) attributes.get("picture"))
        .provider((AuthProvider) attributes.get("provider"))
        .build();
  }
}
