package com.blackcowmoo.moomark.auth.service

import com.blackcowmoo.moomark.auth.model.AuthProvider
import com.blackcowmoo.moomark.auth.model.Role
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TokenServiceTest {

  @Autowired
  private lateinit var tokenService: TokenService

  @Test
  fun `generateToken should create access and refresh tokens`() {
    val id = "test-id"
    val provider = AuthProvider.GOOGLE
    val role = Role.USER

    val token = tokenService.generateToken(id, provider, role)

    assertNotNull(token.token)
    assertNotNull(token.refreshToken)
    assertNotEquals(token.token, token.refreshToken)
  }

  @Test
  fun `verifyToken should return true for valid token`() {
    val id = "test-id"
    val provider = AuthProvider.GOOGLE
    val role = Role.USER

    val token = tokenService.generateToken(id, provider, role)
    val isValid = tokenService.verifyToken(token.token)

    assertTrue(isValid)
  }

  @Test
  fun `verifyToken should return false for invalid token`() {
    val isValid = tokenService.verifyToken("invalid-token")
    assertFalse(isValid)
  }

  @Test
  fun `verifyToken should return false for expired token`() {
    val id = "test-id"
    val provider = AuthProvider.GOOGLE
    val role = Role.USER

    val token = tokenService.generateToken(id, provider, role)
    val isRefreshTokenValid = tokenService.verifyToken(token.refreshToken)

    assertTrue(isRefreshTokenValid)
  }

  @Test
  fun `verifyRefreshToken should return TokenResponse for valid refresh token`() {
    val id = "test-id"
    val provider = AuthProvider.GOOGLE
    val role = Role.USER

    val token = tokenService.generateToken(id, provider, role)
    val response = tokenService.verifyRefreshToken(token.refreshToken)

    assertNotNull(response)
    assertEquals(id, response?.id)
    assertEquals(provider, response?.provider)
    assertEquals(role, response?.role)
  }

  @Test
  fun `verifyRefreshToken should return null for invalid refresh token`() {
    val response = tokenService.verifyRefreshToken("invalid-refresh-token")
    assertNull(response)
  }

  @Test
  fun `getUid should return user id from token`() {
    val id = "test-id"
    val provider = AuthProvider.GOOGLE
    val role = Role.USER

    val token = tokenService.generateToken(id, provider, role)
    val uid = tokenService.getUid(token.token)

    assertEquals(id, uid)
  }

  @Test
  fun `getProvider should return auth provider from token`() {
    val id = "test-id"
    val provider = AuthProvider.GOOGLE
    val role = Role.USER

    val token = tokenService.generateToken(id, provider, role)
    val retrievedProvider = tokenService.getProvider(token.token)

    assertEquals(provider, retrievedProvider)
  }

  @Test
  fun `getTokenType should return token type from token`() {
    val id = "test-id"
    val provider = AuthProvider.GOOGLE
    val role = Role.USER

    val token = tokenService.generateToken(id, provider, role)
    val tokenType = tokenService.getTokenType(token.token)

    assertEquals("access", tokenType)
  }

  @Test
  fun `getTokenType should return refresh for refresh token`() {
    val id = "test-id"
    val provider = AuthProvider.GOOGLE
    val role = Role.USER

    val token = tokenService.generateToken(id, provider, role)
    val tokenType = tokenService.getTokenType(token.refreshToken)

    assertEquals("refresh", tokenType)
  }
}
