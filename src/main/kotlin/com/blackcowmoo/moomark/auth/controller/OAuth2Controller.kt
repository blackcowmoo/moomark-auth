package com.blackcowmoo.moomark.auth.controller

import com.blackcowmoo.moomark.auth.model.AuthProvider
import com.blackcowmoo.moomark.auth.model.Role
import com.blackcowmoo.moomark.auth.model.oauth2.Token
import com.blackcowmoo.moomark.auth.service.TokenService
import com.blackcowmoo.moomark.auth.service.oauth2.GoogleOAuth2Service
import lombok.RequiredArgsConstructor
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletResponse

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/oauth2")
class OAuth2Controller(
  private val tokenService: TokenService,
  private val googleOAuth2Service: GoogleOAuth2Service
) {
  class RefreshTokenRequestBody {
    var refreshToken: String? = null
  }

  @Value("\${environment}")
  private lateinit var environment: String

  @GetMapping("/google")
  fun googleCode(@RequestParam code: String): Token {
    if (environment == "dev" && code.startsWith("test-")) {
      val tokenStrings = code.split("-")
      val id = tokenStrings[1]
      return tokenService.generateToken(id, AuthProvider.TEST, Role.USER)
    }
    val token = googleOAuth2Service.getToken(code)
    val googleUserInfo = googleOAuth2Service.parseIdToken(token)
    return googleOAuth2Service.login(googleUserInfo!!)
  }

  @PostMapping("/refresh")
  fun refreshToken(@RequestBody body: RefreshTokenRequestBody, response: HttpServletResponse): Token? {
    val refreshToken = body.refreshToken
    if (refreshToken == null) {
      response.status = 400
      return null
    }
    val tokenResponse = tokenService.verifyRefreshToken(refreshToken)
    if (tokenResponse != null) {
      val id = tokenResponse.id ?: ""
      val provider = tokenResponse.provider ?: com.blackcowmoo.moomark.auth.model.AuthProvider.EMPTY
      val role = tokenResponse.role ?: com.blackcowmoo.moomark.auth.model.Role.USER
      return tokenService.generateToken(id, provider, role)
    }
    response.status = 401
    return null
  }
}
