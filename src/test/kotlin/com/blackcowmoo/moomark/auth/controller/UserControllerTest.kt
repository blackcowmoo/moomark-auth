package com.blackcowmoo.moomark.auth.controller

import com.blackcowmoo.moomark.auth.model.AuthProvider
import com.blackcowmoo.moomark.auth.model.Role
import com.blackcowmoo.moomark.auth.model.entity.User
import com.blackcowmoo.moomark.auth.model.oauth2.Token
import com.blackcowmoo.moomark.auth.service.TokenService
import com.blackcowmoo.moomark.auth.service.UserService
import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.json.JSONObject
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest {

  @Value("\${resources.user.default-picture}")
  private lateinit var defaultPicture: String

  @Autowired
  private lateinit var mvc: MockMvc

  @Autowired
  private lateinit var mapper: ObjectMapper

  @MockBean
  private lateinit var tokenService: TokenService

  @MockBean
  private lateinit var userService: UserService

  @Test
  @Order(1)
  fun me() {
    val token = Token("test-jwt-token", "test-refresh-token")
    val user1 = User("1234", AuthProvider.TEST, "test@test.com", "test", "https://test.com", Role.USER)

    `when`(tokenService.generateToken("1234", AuthProvider.TEST, Role.USER)).thenReturn(token)
    `when`(tokenService.verifyToken(any())).thenReturn(true)
    `when`(tokenService.getUid(any())).thenReturn("1234")
    `when`(tokenService.getProvider(any())).thenReturn(AuthProvider.TEST)
    `when`(userService.getUserById(AuthProvider.TEST, "1234")).thenReturn(user1)

    val tokenResult = mapper.readValue(
      mvc.perform(get("/api/v1/oauth2/google").param("code", "test-1234"))
        .andExpect(status().isOk())
        .andReturn().response.contentAsString,
      Token::class.java
    )

    assertThat(tokenResult.token).isNotNull()

    val user2 = mapper.readValue(
      mvc.perform(get("/api/v1/user").header("Authorization", tokenResult.token))
        .andExpect(status().isOk())
        .andReturn().response.contentAsString,
      User::class.java
    )

    assertThat(user2.authProvider).isEqualTo(AuthProvider.TEST)
    assertThat(user2.id).isEqualTo("1234")
  }

  @Test
  @Order(2)
  fun user() {
    val token = Token("test-jwt-token", "test-refresh-token")
    val user3 = User("test", AuthProvider.TEST, "test@test.com", "test", "https://test.com", Role.USER)

    `when`(tokenService.generateToken("test", AuthProvider.TEST, Role.USER)).thenReturn(token)
    `when`(tokenService.verifyToken(any())).thenReturn(true)
    `when`(tokenService.getUid(any())).thenReturn("test")
    `when`(tokenService.getProvider(any())).thenReturn(AuthProvider.TEST)
    `when`(userService.getUserById(AuthProvider.TEST, "test")).thenReturn(user3)

    val tokenResult = mapper.readValue(
      mvc.perform(get("/api/v1/oauth2/google").param("code", "test-test"))
        .andExpect(status().isOk())
        .andReturn().response.contentAsString,
      Token::class.java
    )

    assertThat(tokenResult.token).isNotNull()

    val user4 = mapper.readValue(
      mvc.perform(get("/api/v1/user/TEST/test").header("Content-Type", "application/json"))
        .andExpect(status().isOk())
        .andReturn().response.contentAsString,
      User::class.java
    )

    assertThat(user4.authProvider).isEqualTo(AuthProvider.TEST)
    assertThat(user4.id).isEqualTo("test")
  }

  @Test
  @Order(3)
  fun modifyUser() {
    val id = "test"
    val token = Token("test-jwt-token", "test-refresh-token")
    val user5 = User(id, AuthProvider.TEST, "test@test.com", "test", "https://test.com", Role.USER)

    `when`(tokenService.generateToken(id, AuthProvider.TEST, Role.USER)).thenReturn(token)
    `when`(tokenService.verifyToken(any())).thenReturn(true)
    `when`(tokenService.getUid(any())).thenReturn(id)
    `when`(tokenService.getProvider(any())).thenReturn(AuthProvider.TEST)
    `when`(userService.getUserById(AuthProvider.TEST, id)).thenReturn(user5)

    val tokenResult = mapper.readValue(
      mvc.perform(get("/api/v1/oauth2/google").param("code", "test-$id"))
        .andExpect(status().isOk())
        .andReturn().response.contentAsString,
      Token::class.java
    )

    assertThat(tokenResult.token).isNotNull()

    val beforeUser = mapper.readValue(
      mvc.perform(get("/api/v1/user/TEST/test").header("Content-Type", "application/json"))
        .andExpect(status().isOk())
        .andReturn().response.contentAsString,
      User::class.java
    )

    assertThat(beforeUser.authProvider).isEqualTo(AuthProvider.TEST)
    assertThat(beforeUser.id).isEqualTo(id)
    assertThat(beforeUser.nickname).isNotEmpty()
    assertThat(beforeUser.picture).isNotEmpty()

    val requestParams1 = JSONObject()
    requestParams1.put("nickname", null)
    requestParams1.put("picture", null)

    val user7 = mapper.readValue(
      mvc.perform(
        put("/api/v1/user")
          .header("Content-Type", "application/json")
          .header("Authorization", token.token)
          .content(requestParams1.toString())
      )
        .andExpect(status().isOk())
        .andReturn().response.contentAsString,
      User::class.java
    )

    assertThat(user7.authProvider).isEqualTo(AuthProvider.TEST)
    assertThat(user7.id).isEqualTo(id)
    assertThat(user7.nickname).isEqualTo(beforeUser.nickname)
    assertThat(user7.picture).isEqualTo(beforeUser.picture)

    val requestParams2 = JSONObject()
    requestParams2.put("nickname", "")
    requestParams2.put("picture", "")

    val user8 = mapper.readValue(
      mvc.perform(
        put("/api/v1/user")
          .header("Content-Type", "application/json")
          .header("Authorization", token.token)
          .content(requestParams2.toString())
      )
        .andExpect(status().isOk())
        .andReturn().response.contentAsString,
      User::class.java
    )

    assertThat(user8.authProvider).isEqualTo(AuthProvider.TEST)
    assertThat(user8.id).isEqualTo(id)
    assertThat(user8.nickname).isEqualTo(beforeUser.nickname)
    assertThat(user8.picture).isEqualTo(defaultPicture)

    val newNickname = "testNewNickname"
    val newPicture = "https://i.pravatar.cc/300"

    val requestParams3 = JSONObject()
    requestParams3.put("nickname", newNickname)
    requestParams3.put("picture", newPicture)

    val user9 = mapper.readValue(
      mvc.perform(
        put("/api/v1/user")
          .header("Content-Type", "application/json")
          .header("Authorization", token.token)
          .content(requestParams3.toString())
      )
        .andExpect(status().isOk())
        .andReturn().response.contentAsString,
      User::class.java
    )

    assertThat(user9.authProvider).isEqualTo(AuthProvider.TEST)
    assertThat(user9.id).isEqualTo(id)
    assertThat(user9.nickname).isEqualTo(newNickname)
    assertThat(user9.picture).isEqualTo(newPicture)
  }

  @Test
  @Order(Int.MAX_VALUE)
  fun withdraw() {
    val token = Token("test-jwt-token", "test-refresh-token")

    `when`(tokenService.generateToken("1234", AuthProvider.TEST, Role.USER)).thenReturn(token)

    val tokenResult = mapper.readValue(
      mvc.perform(get("/api/v1/oauth2/google").param("code", "test-1234"))
        .andExpect(status().isOk())
        .andReturn().response.contentAsString,
      Token::class.java
    )

    assertThat(tokenResult.token).isNotNull()
    assertThat(tokenResult.refreshToken).isNotNull()
  }
}
