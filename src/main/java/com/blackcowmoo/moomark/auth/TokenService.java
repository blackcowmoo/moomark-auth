package com.blackcowmoo.moomark.auth;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;

import com.blackcowmoo.moomark.auth.model.AuthProvider;
import com.blackcowmoo.moomark.auth.model.Role;
import com.blackcowmoo.moomark.auth.model.Token;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class TokenService {

  @Value("${jwt.secret}")
  private String jwtSecret = null;
  private SecretKey key = null;

  private final long tokenPeriod = 1000L * 60L * 10L;
  private final long refreshPeriod = 1000L * 60L * 60L * 24L * 30L * 3L;

  @PostConstruct
  private void init() {
    key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
  }

  // @Override
  public Token generateToken(String id, AuthProvider provider, Role role) {
    Claims claims = Jwts.claims();
    claims.setSubject(id);
    claims.put("provider", provider);
    claims.put("role", role);

    Date now = new Date();
    return new Token(
        Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(new Date(now.getTime() + tokenPeriod))
            .signWith(key)
            .compact(),
        Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(new Date(now.getTime() + refreshPeriod))
            .signWith(key)
            .compact());
  }

  // @Override
  public boolean verifyToken(String token) {
    try {
      Jws<Claims> claims = Jwts.parserBuilder()
          .setSigningKey(key)
          .build()
          .parseClaimsJws(token);
      return claims.getBody()
          .getExpiration()
          .after(new Date());
    } catch (Exception e) {
      return false;
    }
  }

  // @Override
  public String getUid(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody()
        .getSubject();
  }

  public AuthProvider getProvider(String token) {
    return AuthProvider.getAuthProviderValue((String) Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody()
        .get("provider"));
  }
}
