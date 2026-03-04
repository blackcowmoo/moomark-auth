package com.blackcowmoo.moomark.auth.service.oauth2

import com.blackcowmoo.moomark.auth.model.AuthProvider
import com.blackcowmoo.moomark.auth.model.entity.User
import com.blackcowmoo.moomark.auth.model.oauth2.Token
import com.blackcowmoo.moomark.auth.service.TokenService
import com.blackcowmoo.moomark.auth.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class TestOAuth2Service {

    @Value("\${environment}")
    private lateinit var environment: String

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var tokenService: TokenService

    fun isTest(code: String): Boolean {
        return environment == "dev" && code.startsWith("test-")
    }

    fun login(testCode: String): Token {
        val tokenStrings = testCode.split("-")
        val id = tokenStrings[1]
        var user = userService.getUserById(AuthProvider.TEST, id)
        if (user == null) {
            user = userService.signUp(id, AuthProvider.TEST, "test", "test@blackcowmoo.com",
                "https://www.gravatar.com/avatar/HASH")
        }

        return tokenService.generateToken(user.id(), user.authProvider(), user.role())
    }
}