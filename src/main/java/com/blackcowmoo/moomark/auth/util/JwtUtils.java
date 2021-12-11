package com.blackcowmoo.moomark.auth.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import com.blackcowmoo.moomark.auth.exception.JwtExpireTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.ToString;

@ToString
@Component
public class JwtUtils {

  private static final String SECRET_KEY = "moo_mark";
  private static final Long EXPIRATION_TIME = 30L;

  private JwtUtils() {
    throw new IllegalStateException("This class is utility class");
  }

  /**
   * Create JWT token function
   * 
   * @param userName
   * @param authorities
   * @return
   */
  public static String createJwtToken(String userName, List<GrantedAuthority> authorities) {
    LocalDateTime currentTime = LocalDateTime.now();
    Claims claims = Jwts.claims().setSubject(userName);
    claims.put("roles", authorities.stream().map(Object::toString).collect(Collectors.toList()));
    return Jwts.builder().setClaims(claims)
        .setIssuedAt(Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant()))
        .setExpiration(Date.from(
            currentTime.plusMinutes(EXPIRATION_TIME).atZone(ZoneId.systemDefault()).toInstant()))
        .signWith(SignatureAlgorithm.HS512, SECRET_KEY).compact();
  }

  /**
   * Parsing JWT
   * 
   * @param token
   * @return
   */
  public Jws<Claims> parseToken(String token) {
    Jws<Claims> result = null;
    try {
      result = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
    } catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException
        | SignatureException e) {
      e.printStackTrace();
      throw new BadCredentialsException("Invalid JWT token : ", e);
    } catch (ExpiredJwtException e) {
      e.printStackTrace();
      throw new JwtExpireTokenException("JWT toekn is expired");
    }
    return result;
  }
}
