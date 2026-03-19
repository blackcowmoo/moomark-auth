package com.blackcowmoo.moomark.auth.controller

import com.blackcowmoo.moomark.auth.model.AuthProvider
import com.blackcowmoo.moomark.auth.model.Role
import com.blackcowmoo.moomark.auth.model.dto.PassportResponse
import com.blackcowmoo.moomark.auth.model.entity.User
import com.blackcowmoo.moomark.auth.model.oauth2.Token
import com.blackcowmoo.moomark.auth.service.PassportService
import com.blackcowmoo.moomark.auth.service.TokenService
import com.blackcowmoo.moomark.auth.service.UserService
import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.anyString
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(locations = ["classpath:application-test.yaml"])
class PssportControllerTest {

  @Value("\${passport.public-key}")
  private lateinit var passportPublicKey: String

  @Value("\${passport.test.token.expired.user}")
  private lateinit var expiredTestPassportUser: String

  @Value("\${passport.test.token.expired.key}")
  private lateinit var expiredTestPassportKey: String

  @Autowired
  private lateinit var mvc: MockMvc

  @Autowired
  private lateinit var mapper: ObjectMapper

  @MockBean
  private lateinit var tokenService: TokenService

  @MockBean
  private lateinit var userService: UserService

  @MockBean
  private lateinit var passportService: PassportService

  @Test
  fun generatePassport() {
    val userId = "1234"
    val token = Token("test-jwt-token", "test-refresh-token")
    val user1 = User(userId, AuthProvider.TEST, "test@test.com", "test", "https://test.com", Role.USER)

    `when`(tokenService.generateToken(userId, AuthProvider.TEST, Role.USER)).thenReturn(token)
    `when`(tokenService.verifyToken(anyString())).thenReturn(true)
    `when`(tokenService.getUid(anyString())).thenReturn(userId)
    `when`(tokenService.getProvider(anyString())).thenReturn(AuthProvider.TEST)
    `when`(userService.getUserById(AuthProvider.TEST, userId)).thenReturn(user1)
    `when`(passportService.generatePassport(any())).thenReturn(null)

    val tokenResult = mapper.readValue(
      mvc.perform(get("/api/v1/oauth2/google").param("code", "test-$userId"))
        .andExpect(status().isOk())
        .andReturn().response.contentAsString,
      Token::class.java
    )

    assertThat(tokenResult.token).isNotNull()

    val passport = mapper.readValue(
      mvc.perform(get("/api/v1/passport").header("Authorization", tokenResult.token))
        .andExpect(status().isOk())
        .andReturn().response.contentAsString,
      PassportResponse::class.java
    )

    val user2 = mapper.readValue(
      mvc.perform(
        get("/api/v1/passport/verify")
          .header("x-moom-passport-user", passport.passport)
          .header("x-moom-passport-key", passport.key)
      )
        .andExpect(status().isOk())
        .andReturn().response.contentAsString,
      User::class.java
    )

    assertThat(user2.id).isEqualTo(userId)
  }

  @Test
  fun verifyPassport() {
    val userId = "1234"
    val token = Token("test-jwt-token", "test-refresh-token")
    val user3 = User(userId, AuthProvider.TEST, "test@test.com", "test", "https://test.com", Role.USER)

    `when`(tokenService.generateToken(userId, AuthProvider.TEST, Role.USER)).thenReturn(token)
    `when`(tokenService.verifyToken(anyString())).thenReturn(true)
    `when`(tokenService.getUid(anyString())).thenReturn(userId)
    `when`(tokenService.getProvider(anyString())).thenReturn(AuthProvider.TEST)
    `when`(userService.getUserById(AuthProvider.TEST, userId)).thenReturn(user3)

    val tokenResult = mapper.readValue(
      mvc.perform(get("/api/v1/oauth2/google").param("code", "test-$userId"))
        .andExpect(status().isOk())
        .andReturn().response.contentAsString,
      Token::class.java
    )

    assertThat(tokenResult.token).isNotNull()

    val passport = mapper.readValue(
      mvc.perform(get("/api/v1/passport").header("Authorization", token.token))
        .andExpect(status().isOk())
        .andReturn().response.contentAsString,
      PassportResponse::class.java
    )

    val user4 = mapper.readValue(
      mvc.perform(
        get("/api/v1/user")
          .header("x-moom-passport-user", passport.passport)
          .header("x-moom-passport-key", passport.key)
      )
        .andExpect(status().isOk())
        .andReturn().response.contentAsString,
      User::class.java
    )

    assertThat(user4.id).isEqualTo(userId)
  }

  @Test
  fun checkPublicKey() {
    val publicKey = passportPublicKey
    val testPublicKey = mvc.perform(get("/api/v1/passport/verify/public"))
      .andExpect(status().isOk())
      .andReturn().response.contentAsString

    assertThat(publicKey).isNotNull()
    assertThat(publicKey).isNotEmpty()
    assertThat(publicKey).isEqualTo(testPublicKey)
  }

  @Test
  fun expiredPassport() {
    val response = mvc.perform(
      get("/api/v1/passport/verify")
        .header("x-moom-passport-user", expiredTestPassportUser)
        .header("x-moom-passport-key", expiredTestPassportKey)
    )
      .andExpect(status().isOk())
      .andReturn().response.contentAsString

    assertThat(response).isEmpty()
  }
}
