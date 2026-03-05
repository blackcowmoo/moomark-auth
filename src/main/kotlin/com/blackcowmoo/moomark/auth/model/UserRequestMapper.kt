package com.blackcowmoo.moomark.auth.model

import com.blackcowmoo.moomark.auth.model.dto.UserDto
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Component

@Component
class UserRequestMapper {
    fun toDto(oAuth2User: OAuth2User): UserDto {
        val attributes = oAuth2User.attributes
        return UserDto.builder()
            .id(attributes["id"] as String?)
            .email(attributes["email"] as String?)
            .name(attributes["name"] as String?)
            .picture(attributes["picture"] as String?)
            .provider(attributes["provider"] as AuthProvider?)
            .build()
    }
}