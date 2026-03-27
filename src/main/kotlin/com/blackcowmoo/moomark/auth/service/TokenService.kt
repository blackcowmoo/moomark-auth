package com.blackcowmoo.moomark.auth.service

import com.blackcowmoo.moomark.auth.model.AuthProvider
import com.blackcowmoo.moomark.auth.model.Role
import com.blackcowmoo.moomark.auth.model.dto.TokenResponse
import com.blackcowmoo.moomark.auth.model.oauth2.Token
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.util.Date
import javax.annotation.PostConstruct
import javax.crypto.SecretKey

@Service
open class TokenService {

  @Value("\${jwt.secret}")
  private var jwtSecret: String? = null
  private var key: SecretKey? = null

  private val tokenPeriod: Long = 1000L * 60L * 60L
  private val refreshPeriod: Long = 1000L * 60L * 60L * 24L * 30L * 3L

  private companion object {
    const val PROVIDER_KEY = "provider"
    const val ROKE_KEY = "role"
    const val TOKEN_KEY = "token"
    const val REFRESH_TOKEN_VALUE = "refresh"
  }

  @PostConstruct
  private fun init() {
    key = Keys.hmacShaKeyFor(jwtSecret?.toByteArray(StandardCharsets.UTF_8))
  }

  open fun generateToken(id: String, provider: AuthProvider, role: Role): Token {
    val now = Date()

    val accessClaims = Jwts.claims()
    accessClaims.subject = id
    accessClaims[PROVIDER_KEY] = provider
    accessClaims[ROKE_KEY] = role
    accessClaims[TOKEN_KEY] = "access"

    val token = Jwts.builder()
      .setClaims(accessClaims)
      .setIssuedAt(now)
      .setExpiration(Date(now.time + tokenPeriod))
      .signWith(key)
      .compact()

    val refreshClaims = Jwts.claims()
    refreshClaims.subject = id
    refreshClaims[PROVIDER_KEY] = provider
    refreshClaims[ROKE_KEY] = role
    refreshClaims[TOKEN_KEY] = REFRESH_TOKEN_VALUE

    val refreshToken = Jwts.builder()
      .setClaims(refreshClaims)
      .setIssuedAt(now)
      .setExpiration(Date(now.time + refreshPeriod))
      .signWith(key)
      .compact()

    return Token(token, refreshToken)
  }

  open fun verifyToken(token: String): Boolean {
    return try {
      val claims: Jws<Claims> = Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
      claims.body.expiration.after(Date())
    } catch (e: Exception) {
      false
    }
  }

  open fun verifyRefreshToken(refreshToken: String): TokenResponse? {
    return try {
      val claims: Jws<Claims> = Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(refreshToken)
      val claimsBody = claims.body

      if (claimsBody.expiration.after(Date()) && claimsBody[TOKEN_KEY, String::class.java] == REFRESH_TOKEN_VALUE) {
        val response = TokenResponse()
        response.id = claimsBody.subject
        response.provider = AuthProvider.valueOf(claimsBody[PROVIDER_KEY, String::class.java])
        response.role = Role.valueOf(claimsBody[ROKE_KEY, String::class.java])
        return response
      } else {
        null
      }
    } catch (e: Exception) {
      null
    }
  }

  open fun getUid(token: String): String {
    return Jwts.parserBuilder()
      .setSigningKey(key)
      .build()
      .parseClaimsJws(token)
      .body
      .subject
  }

  open fun getProvider(token: String): AuthProvider {
    return AuthProvider.getAuthProviderValue(
      Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .body
        .get("provider", String::class.java)
    )
  }

  open fun getTokenType(token: String): String {
    return Jwts.parserBuilder()
      .setSigningKey(key)
      .build()
      .parseClaimsJws(token)
      .body
      .get("token", String::class.java)
  }
}
