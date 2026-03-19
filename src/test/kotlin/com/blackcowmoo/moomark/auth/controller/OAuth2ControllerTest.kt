package com.blackcowmoo.moomark.auth.controller

import com.blackcowmoo.moomark.auth.model.AuthProvider
import com.blackcowmoo.moomark.auth.model.Role
import com.blackcowmoo.moomark.auth.model.entity.User
import com.blackcowmoo.moomark.auth.model.oauth2.Token
import com.blackcowmoo.moomark.auth.service.PassportService
import com.blackcowmoo.moomark.auth.service.TokenService
import com.blackcowmoo.moomark.auth.service.UserService
import com.blackcowmoo.moomark.auth.service.oauth2.GoogleOAuth2Service
import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.json.JSONObject
import org.junit.jupiter.api.Test
import org.mockito.Mockito.anyString
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(locations = ["classpath:application-test.yaml"])
class OAuth2ControllerTest {

  @Autowired
  private lateinit var mvc: MockMvc

  @Autowired
  private lateinit var mapper: ObjectMapper

  @MockBean
  private lateinit var googleOAuth2Service: GoogleOAuth2Service

  @MockBean
  private lateinit var tokenService: TokenService

  @MockBean
  private lateinit var userService: UserService

  @MockBean
  private lateinit var passportService: PassportService

  @Test
  fun testGoogleCode() {
    val token = Token("test-jwt-token", "test-refresh-token")
    val user = User("1234", AuthProvider.TEST, "test@test.com", "test", "https://test.com", Role.USER)

    `when`(tokenService.generateToken("1234", AuthProvider.TEST, Role.USER)).thenReturn(token)
    `when`(tokenService.verifyToken(anyString())).thenReturn(true)
    `when`(tokenService.getUid(anyString())).thenReturn("1234")
    `when`(tokenService.getProvider(anyString())).thenReturn(AuthProvider.TEST)
    `when`(userService.getUserById(AuthProvider.TEST, "1234")).thenReturn(user)

    val responseToken = mapper.readValue(
      mvc.perform(get("/api/v1/oauth2/google").param("code", "test-1234"))
        .andExpect(status().isOk())
        .andReturn().response.contentAsString,
      Token::class.java
    )

    assertThat(responseToken.token).isEqualTo("test-jwt-token")
    assertThat(responseToken.refreshToken).isEqualTo("test-refresh-token")
  }

  @Test
  fun failRefreshToken() {
    val token = Token("test-jwt-token", "test-refresh-token")
    val user = User("1234", AuthProvider.TEST, "test@test.com", "test", "https://test.com", Role.USER)

    `when`(tokenService.generateToken("1234", AuthProvider.TEST, Role.USER)).thenReturn(token)
    `when`(tokenService.verifyToken(anyString())).thenReturn(true)
    `when`(tokenService.getUid(anyString())).thenReturn("1234")
    `when`(tokenService.getProvider(anyString())).thenReturn(AuthProvider.TEST)
    `when`(userService.getUserById(AuthProvider.TEST, "1234")).thenReturn(user)

    val responseToken = mapper.readValue(
      mvc.perform(get("/api/v1/oauth2/google").param("code", "test-1234"))
        .andExpect(status().isOk())
        .andReturn().response.contentAsString,
      Token::class.java
    )

    val requestParams = JSONObject()
    requestParams.put("refreshToken", "invalid-token")

    mvc.perform(
      post("/api/v1/oauth2/refresh").header("Content-Type", "application/json")
        .content(requestParams.toString())
    )
      .andExpect(status().`is`(401))
  }

  @Test
  fun refreshToken() {
    val token = Token("test-jwt-token", "test-refresh-token")
    val newToken = Token("new-jwt-token", "new-refresh-token")
    val user = User("1234", AuthProvider.TEST, "test@test.com", "test", "https://test.com", Role.USER)

    `when`(tokenService.generateToken("1234", AuthProvider.TEST, Role.USER)).thenReturn(token)
    `when`(tokenService.generateToken("1234", AuthProvider.TEST, Role.USER)).thenReturn(newToken)
    `when`(tokenService.verifyToken(anyString())).thenReturn(true)
    `when`(tokenService.getUid(anyString())).thenReturn("1234")
    `when`(tokenService.getProvider(anyString())).thenReturn(AuthProvider.TEST)
    `when`(userService.getUserById(AuthProvider.TEST, "1234")).thenReturn(user)

    val responseToken = mapper.readValue(
      mvc.perform(get("/api/v1/oauth2/google").param("code", "test-1234"))
        .andExpect(status().isOk())
        .andReturn().response.contentAsString,
      Token::class.java
    )

    val requestParams = JSONObject()
    requestParams.put("refreshToken", "test-refresh-token")

    val newTokenResult = mapper.readValue(
      mvc.perform(
        post("/api/v1/oauth2/refresh").header("Content-Type", "application/json")
          .content(requestParams.toString())
      )
        .andExpect(status().isOk())
        .andReturn().response.contentAsString,
      Token::class.java
    )

    assertThat(newTokenResult.token).isEqualTo("new-jwt-token")
    assertThat(newTokenResult.refreshToken).isEqualTo("new-refresh-token")
  }
}
