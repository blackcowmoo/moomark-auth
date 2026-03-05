package com.blackcowmoo.moomark.auth.service.oauth2

import com.blackcowmoo.moomark.auth.model.AuthProvider
import com.blackcowmoo.moomark.auth.model.entity.User
import com.blackcowmoo.moomark.auth.model.oauth2.GoogleTokenResponse
import com.blackcowmoo.moomark.auth.model.oauth2.GoogleTokenResult
import com.blackcowmoo.moomark.auth.model.oauth2.Token
import com.blackcowmoo.moomark.auth.service.TokenService
import com.blackcowmoo.moomark.auth.service.UserService
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.util.Base64

@Service
class GoogleOAuth2Service {

    @Autowired
    private lateinit var restTemplate: RestTemplate

    @Autowired
    private lateinit var mapper: ObjectMapper

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var tokenService: TokenService

    @Value("\${spring.security.oauth2.client.registration.google.client-id}")
    private lateinit var clientId: String

    @Value("\${spring.security.oauth2.client.registration.google.client-secret}")
    private lateinit var clientSecret: String

    @Value("\${spring.security.oauth2.client.registration.google.redirect-uri}")
    private lateinit var redirectUri: String

    @Value("\${spring.security.oauth2.client.registration.google.authorization-grant-type}")
    private lateinit var grantType: String

    fun getToken(code: String): String {
        val parameters = mapOf(
            "client_id" to clientId,
            "client_secret" to clientSecret,
            "redirect_uri" to redirectUri,
            "grant_type" to grantType,
            "code" to code
        )

        val result = restTemplate.postForObject(
            "https://oauth2.googleapis.com/token",
            parameters,
            GoogleTokenResponse::class.java
        )

        if (result?.isExpired() == true) {
            throw RuntimeException("ExpiredGoogleCode")
        }

        return result!!.idToken!!
    }

    fun parseIdToken(idToken: String): GoogleTokenResult? {
        val body = String(Base64.getDecoder().decode(idToken.split("\\.".toRegex())[1]))
        return try {
            mapper.readValue(body, GoogleTokenResult::class.java)
        } catch (e: JsonProcessingException) {
            e.printStackTrace()
            null
        }
    }

    fun login(googleUser: GoogleTokenResult): Token {
        val user = userService.getUserById(AuthProvider.GOOGLE, googleUser.sub)
        val userToLogin = user ?: userService.signUp(
            googleUser.sub ?: "",
            AuthProvider.GOOGLE,
            googleUser.name ?: "",
            googleUser.email ?: "",
            googleUser.picture ?: ""
        )

        return tokenService.generateToken(userToLogin.id ?: "", userToLogin.authProvider ?: AuthProvider.EMPTY, userToLogin.role ?: Role.USER)
    }
}