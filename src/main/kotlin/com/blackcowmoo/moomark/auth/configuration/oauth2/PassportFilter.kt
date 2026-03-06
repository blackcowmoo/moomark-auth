package com.blackcowmoo.moomark.auth.configuration.oauth2

import com.blackcowmoo.moomark.auth.model.entity.User
import com.blackcowmoo.moomark.auth.service.PassportService
import lombok.RequiredArgsConstructor
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.GenericFilterBean
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

@RequiredArgsConstructor
class PassportFilter(private val passportService: PassportService) : GenericFilterBean() {

  @Throws(ServletException::class, IOException::class)
  override fun doFilter(
    request: ServletRequest,
    response: ServletResponse,
    chain: FilterChain
  ) {
    val passport = (request as HttpServletRequest).getHeader("x-moom-passport-user")
    val key = (request as HttpServletRequest).getHeader("x-moom-passport-key")

    if (passport != null && key != null) {
      val user = passportService.parsePassport(passport, key)
      if (user != null) {
        val auth = getAuthentication(user)
        SecurityContextHolder.getContext().authentication = auth
      }
    }

    chain.doFilter(request, response)
  }

  private fun getAuthentication(user: User): Authentication {
    return UsernamePasswordAuthenticationToken(user, "", listOf(SimpleGrantedAuthority("ROLE_USER")))
  }
}
