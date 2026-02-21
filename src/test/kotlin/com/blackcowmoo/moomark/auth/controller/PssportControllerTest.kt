package com.blackcowmoo.moomark.auth.controller

import com.blackcowmoo.moomark.auth.model.dto.PassportResponse
import com.blackcowmoo.moomark.auth.model.entity.User
import com.blackcowmoo.moomark.auth.model.oauth2.Token
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class PssportControllerTest {
    @Value("\${passport.public-key}")
    lateinit var passportPublicKey: String

    @Value("\${passport.test.token.expired.user}")
    lateinit var expiredTestPassportUser: String

    @Value("\${passport.test.token.expired.key}")
    lateinit var expiredTestPassportKey: String

    @Autowired
    lateinit var mvc: MockMvc

    @Autowired
    lateinit var mapper: ObjectMapper

    @Test
    fun generatePassport() {
        val userId = "1234"
        val token = mapper.readValue(
            mvc.perform(get("/api/v1/oauth2/google").param("code", "test-$userId"))
                .andExpect(status().isOk())
                .andReturn().response.contentAsString,
            Token::class.java
        )

        assertNotNull(token.token)

        val passport = mapper.readValue(
            mvc.perform(get("/api/v1/passport").header("Authorization", token.token))
                .andExpect(status().isOk())
                .andReturn().response.contentAsString,
            PassportResponse::class.java
        )

        val user = mapper.readValue(
            mvc.perform(
                get("/api/v1/passport/verify")
                    .header("x-moom-passport-user", passport.passport)
                    .header("x-moom-passport-key", passport.key)
            )
                .andExpect(status().isOk())
                .andReturn().response.contentAsString,
            User::class.java
        )
        assertEquals(user.id, userId)
    }

    @Test
    fun verifyPassport() {
        val userId = "1234"
        val token = mapper.readValue(
            mvc.perform(get("/api/v1/oauth2/google").param("code", "test-$userId"))
                .andExpect(status().isOk())
                .andReturn().response.contentAsString,
            Token::class.java
        )

        assertNotNull(token.token)

        val passport = mapper.readValue(
            mvc.perform(get("/api/v1/passport").header("Authorization", token.token))
                .andExpect(status().isOk())
                .andReturn().response.contentAsString,
            PassportResponse::class.java
        )

        val user = mapper.readValue(
            mvc.perform(
                get("/api/v1/user")
                    .header("x-moom-passport-user", passport.passport)
                    .header("x-moom-passport-key", passport.key)
            )
                .andExpect(status().isOk())
                .andReturn().response.contentAsString,
            User::class.java
        )
        assertEquals(user.id, userId)
    }

    @Test
    fun checkPublicKey() {
        val publicKey = passportPublicKey
        val testPublicKey = mvc.perform(get("/api/v1/passport/verify/public"))
            .andExpect(status().isOk())
            .andReturn().response.contentAsString

        assertNotNull(publicKey)
        assertNotEquals(publicKey, "")
        assertEquals(publicKey, testPublicKey)
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

        assertEquals(response, "")
    }
}
