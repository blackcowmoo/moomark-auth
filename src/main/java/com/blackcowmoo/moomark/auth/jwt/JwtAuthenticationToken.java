package com.blackcowmoo.moomark.auth.jwt;

import java.util.Collection;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {
  private static final long serialVersionUID = -4965638461949991941L;
  private final Jws<Claims> claimsJws;
  private final String email;


  public JwtAuthenticationToken(String email, Collection<? extends GrantedAuthority> authorities) {
    super(authorities);
    this.eraseCredentials();
    this.email = email;
    super.setAuthenticated(true);
    this.claimsJws = null;
  }

  public JwtAuthenticationToken(Jws<Claims> claimsJws) {
    super(null);
    this.claimsJws = claimsJws;
    this.setAuthenticated(false);
    this.email = null;
  }

  @Override
  public Object getCredentials() {
    return this.claimsJws;
  }

  @Override
  public Object getPrincipal() {
    return email;
  }

}
