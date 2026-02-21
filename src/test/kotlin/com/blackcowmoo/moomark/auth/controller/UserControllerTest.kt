package com.blackcowmoo.moomark.auth.controller

import com.blackcowmoo.moomark.auth.model.AuthProvider
import com.blackcowmoo.moomark.auth.model.entity.User
import com.blackcowmoo.moomark.auth.model.oauth2.Token
import com.fasterxml.jackson.databind.ObjectMapper
import com.nimbusds.jose.shaded.json.JSONObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.junit.jupiter.api.MethodOrderer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Value("\${resources.user.default-picture}")
    lateinit var defaultPicture: String

    @Autowired
    lateinit var mvc: MockMvc

    @Autowired
    lateinit var mapper: ObjectMapper

    @Test
    @Order(1)
    fun me() {
        val token = mapper.readValue(
            mvc.perform(get("/api/v1/oauth2/google").param("code", "test-1234"))
                .andExpect(status().isOk())
                .andReturn().response.contentAsString,
            Token::class.java
        )

        assertNotNull(token.token)

        val user = mapper.readValue(
            mvc.perform(get("/api/v1/user").header("Authorization", token.token))
                .andExpect(status().isOk())
                .andReturn().response.contentAsString,
            User::class.java
        )

        assertEquals(user.authProvider, AuthProvider.TEST)
        assertEquals(user.id, "1234")
    }

    @Test
    @Order(2)
    fun user() {
        val token = mapper.readValue(
            mvc.perform(get("/api/v1/oauth2/google").param("code", "test-test"))
                .andExpect(status().isOk())
                .andReturn().response.contentAsString,
            Token::class.java
        )

        assertNotNull(token.token)

        val user = mapper.readValue(
            mvc.perform(get("/api/v1/user/TEST/test").header("Authorization", token.token))
                .andExpect(status().isOk())
                .andReturn().response.contentAsString,
            User::class.java
        )

        assertEquals(user.authProvider, AuthProvider.TEST)
        assertEquals(user.id, "test")
    }

    @Test
    @Order(3)
    fun modifyUser() {
        val id = "test"
        val token = mapper.readValue(
            mvc.perform(get("/api/v1/oauth2/google").param("code", "test-$id"))
                .andExpect(status().isOk())
                .andReturn().response.contentAsString,
            Token::class.java
        )

        assertNotNull(token.token)

        val beforeUser = mapper.readValue(
            mvc.perform(get("/api/v1/user/TEST/test").header("Authorization", token.token))
                .andExpect(status().isOk())
                .andReturn().response.contentAsString,
            User::class.java
        )

        assertEquals(beforeUser.authProvider, AuthProvider.TEST)
        assertEquals(beforeUser.id, id)
        assertNotEquals(beforeUser.nickname, "")
        assertNotEquals(beforeUser.picture, "")

        val requestParams1 = JSONObject()
        requestParams1.put("nickname", null)
        requestParams1.put("picture", null)

        val user1 = mapper.readValue(
            mvc.perform(put("/api/v1/user")
                .header("Authorization", token.token)
                .content(requestParams1.toJSONString()))
                .andExpect(status().isOk())
                .andReturn().response.contentAsString,
            User::class.java
        )

        assertEquals(user1.authProvider, AuthProvider.TEST)
        assertEquals(user1.id, id)
        assertEquals(user1.nickname, beforeUser.nickname)
        assertEquals(user1.picture, beforeUser.picture)

        val requestParams2 = JSONObject()
        requestParams2.put("nickname", "")
        requestParams2.put("picture", "")

        val user2 = mapper.readValue(
            mvc.perform(put("/api/v1/user")
                .header("Authorization", token.token)
                .content(requestParams2.toJSONString()))
                .andExpect(status().isOk())
                .andReturn().response.contentAsString,
            User::class.java
        )

        assertEquals(user2.authProvider, AuthProvider.TEST)
        assertEquals(user2.id, id)
        assertEquals(user2.nickname, beforeUser.nickname)
        assertEquals(user2.picture, defaultPicture)

        val newNickname = "testNewNickname"
        val newPicture = "https://i.pravatar.cc/300"

        val requestParams3 = JSONObject()
        requestParams3.put("nickname", newNickname)
        requestParams3.put("picture", newPicture)

        val user3 = mapper.readValue(
            mvc.perform(put("/api/v1/user")
                .header("Authorization", token.token)
                .content(requestParams3.toJSONString()))
                .andExpect(status().isOk())
                .andReturn().response.contentAsString,
            User::class.java
        )

        assertEquals(user3.authProvider, AuthProvider.TEST)
        assertEquals(user3.id, id)
        assertEquals(user3.nickname, newNickname)
        assertEquals(user3.picture, newPicture)
    }

    @Test
    @Order(Int.MAX_VALUE)
    fun withdraw() {
        val token = mapper.readValue(
            mvc.perform(get("/api/v1/oauth2/google").param("code", "test-1234"))
                .andExpect(status().isOk())
                .andReturn().response.contentAsString,
            Token::class.java
        )

        assertNotNull(token.token)
        assertNotNull(token.refreshToken)
    }
}
