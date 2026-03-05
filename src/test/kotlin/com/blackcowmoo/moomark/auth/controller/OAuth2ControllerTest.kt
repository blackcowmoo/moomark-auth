package com.blackcowmoo.moomark.auth.controller

import com.blackcowmoo.moomark.auth.model.oauth2.Token
import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.not
import org.json.JSONObject
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
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var mapper: ObjectMapper

    @Test
    fun testGoogleCode() {
        val token = mapper.readValue(
            mvc.perform(get("/api/v1/oauth2/google").param("code", "test-1234"))
                .andExpect(status().isOk())
                .andReturn().response.contentAsString,
            Token::class.java
        )

        assertThat(token.token).isNotNull()
        assertThat(token.refreshToken).isNotNull()
    }

    @Test
    fun failRefreshToken() {
        val token = mapper.readValue(
            mvc.perform(get("/api/v1/oauth2/google").param("code", "test-1234"))
                .andExpect(status().isOk())
                .andReturn().response.contentAsString,
            Token::class.java
        )

        assertThat(token.token).isNotNull()
        assertThat(token.refreshToken).isNotNull()
        assertThat(token.token).isNotEmpty()
        assertThat(token.refreshToken).isNotEmpty()

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

        assertThat(token.token).isNotNull()
        assertThat(token.refreshToken).isNotNull()
        assertThat(token.token).isNotEmpty()
        assertThat(token.refreshToken).isNotEmpty()

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

        assertThat(newToken.token).isNotNull()
        assertThat(newToken.refreshToken).isNotNull()
        assertThat(newToken.token).isNotEmpty()
        assertThat(newToken.refreshToken).isNotEmpty()
        assertThat(newToken.token).isNotEqualTo(token.token)
        assertThat(newToken.refreshToken).isNotEqualTo(token.refreshToken)
    }
}