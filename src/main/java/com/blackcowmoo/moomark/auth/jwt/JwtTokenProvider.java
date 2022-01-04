package com.blackcowmoo.moomark.auth.jwt;

import java.util.Base64;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import com.blackcowmoo.moomark.auth.model.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
  private String privateKey = null;
  private static final String SEED = "moo-mark";
  private static final long ACCESS_TOKEN_VALID_TIME = 30 * 60 * 1000L; // 30min
  private static final long REFRESH_TOKEN_VALID_TIME = 60 * 60 * 24 * 14 * 1000L; // 2weeks
  private final UserDetailsService userDetailsService;

  @PostConstruct
  void init() {
    log.debug("create pk for jwt token provider");
    privateKey = Base64.getEncoder().encodeToString(SEED.getBytes());
  }

  public String createJwtAccessToken(String userId, Role role) {
    Claims claims = Jwts.claims().setSubject(userId);
    claims.put("roles", role);
    Date now = new Date();
    Date expiration = new Date(now.getTime() + ACCESS_TOKEN_VALID_TIME);

    return Jwts.builder().setClaims(claims).setIssuedAt(now).setExpiration(expiration)
        .signWith(SignatureAlgorithm.HS256, privateKey).compact();
  }

  public String createJwtRefreshToken(String value) {
    Claims claims = Jwts.claims();
    claims.put("value", value);
    Date now = new Date();
    Date expiration = new Date(now.getTime() + REFRESH_TOKEN_VALID_TIME);

    return Jwts.builder().setClaims(claims).setIssuedAt(now).setExpiration(expiration)
        .signWith(SignatureAlgorithm.HS256, privateKey).compact();
  }

  public String resolveJwtToken(HttpServletRequest request) {
    return request.getHeader("Authorization");
  }

  public Authentication getAuthentication(String token) {
    UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserId(token));
    return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
  }

  public String getUserId(String token) {
    return getClaimsFromJwtToken(token).getBody().getSubject();
  }

  public boolean isTokenValid(String jwtToken) {
    try {
      Jws<Claims> claims = getClaimsFromJwtToken(jwtToken);
      return !claims.getBody().getExpiration().before(new Date());
    } catch (Exception e) {
      return false;
    }
  }

  public Jws<Claims> getClaimsFromJwtToken(String jwtToken) throws JwtException {
    return Jwts.parser().setSigningKey(privateKey).parseClaimsJws(jwtToken);
  }
}
