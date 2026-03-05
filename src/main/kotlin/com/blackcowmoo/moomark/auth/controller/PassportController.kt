package com.blackcowmoo.moomark.auth.controller

import com.blackcowmoo.moomark.auth.model.dto.PassportResponse
import com.blackcowmoo.moomark.auth.model.entity.User
import com.blackcowmoo.moomark.auth.service.PassportService
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletResponse

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/passport")
class PassportController(
    private val passportService: PassportService
) {
    private fun getUser(): User {
        return SecurityContextHolder.getContext().authentication.principal as User
    }

    @GetMapping("/verify/public")
    fun getMyInfo(): String {
        return passportService.getPublicKeyString()
    }

    @GetMapping("/verify")
    fun verifyPassport(
        @RequestHeader("x-moom-passport-user") passport: String,
        @RequestHeader("x-moom-passport-key") key: String
    ): User {
        return passportService.parsePassport(passport, key)
    }

    @GetMapping
    fun generatePassport(response: HttpServletResponse): PassportResponse? {
        val passport = passportService.generatePassport(getUser())
        if (passport == null) {
            response.status = 401
            return null
        }
        return passport
    }
}