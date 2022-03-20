package com.blackcowmoo.moomark.auth.configuration.oauth2;

import java.util.HashMap;
import java.util.Map;

import com.blackcowmoo.moomark.auth.model.AuthProvider;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Builder(access = AccessLevel.PRIVATE)
@Getter
class OAuth2Attribute {
  private Map<String, Object> attributes;
  private String attributeKey;
  private String email;
  private String name;
  private String picture;
  private AuthProvider provider;

  static OAuth2Attribute of(String provider, Map<String, Object> attributes) {
    switch (provider) {
      case "google":
        return ofGoogle(attributes);
      default:
        throw new RuntimeException();
    }
  }

  private static OAuth2Attribute ofGoogle(Map<String, Object> attributes) {
    return OAuth2Attribute.builder()
        .name((String) attributes.get("name"))
        .email((String) attributes.get("email"))
        .picture((String) attributes.get("picture"))
        .provider(AuthProvider.GOOGLE)
        .attributes(attributes)
        .attributeKey("sub")
        .build();
  }

  Map<String, Object> convertToMap() {
    Map<String, Object> map = new HashMap<>();
    map.put("id", attributes.get(attributeKey));
    map.put("key", attributeKey);
    map.put("name", name);
    map.put("email", email);
    map.put("picture", picture);
    map.put("provider", provider);

    return map;
  }
}
