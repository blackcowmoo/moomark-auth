package com.blackcowmoo.moomark.auth.controller

import com.blackcowmoo.moomark.auth.model.oauth2.Token
import com.fasterxml.jackson.databind.ObjectMapper
import com.nimbusds.jose.shaded.json.JSONObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class OAuth2ControllerTest {
    @Autowired
    lateinit var mvc: MockMvc

    @Autowired
    lateinit var mapper: ObjectMapper

    @Test
    fun testGoogleCode() {
        val token = mapper.readValue(
            mvc.perform(get("/api/v1/oauth2/google").param("code", "test-1234"))
                .andExpect(status().isOk())
                .andReturn().response.contentAsString,
            Token::class.java
        )

        assertNotNull(token.token)
        assertNotNull(token.refreshToken)
    }

    @Test
    fun failRefreshToken() {
        val token = mapper.readValue(
            mvc.perform(get("/api/v1/oauth2/google").param("code", "test-1234"))
                .andExpect(status().isOk())
                .andReturn().response.contentAsString,
            Token::class.java
        )

        assertNotNull(token.token)
        assertNotNull(token.refreshToken)
        assertNotEquals(token.token, "")
        assertNotEquals(token.refreshToken, "")

        val requestParams = JSONObject()
        requestParams.put("refreshToken", token.token)

        mvc.perform(post("/api/v1/oauth2/refresh").header("Content-Type", "application/json")
            .content(requestParams.toString()))
            .andExpect(status().is(401))
            .andReturn().response.contentAsString
    }

    @Test
    fun refreshToken() {
        val token = mapper.readValue(
            mvc.perform(get("/api/v1/oauth2/google").param("code", "test-1234"))
                .andExpect(status().isOk())
                .andReturn().response.contentAsString,
            Token::class.java
        )

        assertNotNull(token.token)
        assertNotNull(token.refreshToken)
        assertNotEquals(token.token, "")
        assertNotEquals(token.refreshToken, "")

        Thread.sleep(1000)

        val requestParams = JSONObject()
        requestParams.put("refreshToken", token.refreshToken)

        val newToken = mapper.readValue(
            mvc.perform(post("/api/v1/oauth2/refresh").header("Content-Type", "application/json")
                .content(requestParams.toString()))
                .andExpect(status().isOk())
                .andReturn().response.contentAsString,
            Token::class.java
        )

        assertNotNull(newToken.token)
        assertNotNull(newToken.refreshToken)
        assertNotEquals(newToken.token, "")
        assertNotEquals(newToken.refreshToken, "")
        assertNotEquals(token.token, newToken.token)
        assertNotEquals(token.refreshToken, newToken.refreshToken)
    }
}
