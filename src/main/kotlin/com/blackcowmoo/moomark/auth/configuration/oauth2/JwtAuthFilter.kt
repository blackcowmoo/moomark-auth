package com.blackcowmoo.moomark.auth.configuration.oauth2

import com.blackcowmoo.moomark.auth.model.AuthProvider
import com.blackcowmoo.moomark.auth.model.entity.User
import com.blackcowmoo.moomark.auth.service.TokenService
import com.blackcowmoo.moomark.auth.service.UserService
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.GenericFilterBean
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

open class JwtAuthFilter(
    private val tokenService: TokenService,
    private val userService: UserService
) : GenericFilterBean() {

    override fun doFilter(
        request: ServletRequest,
        response: ServletResponse,
        chain: FilterChain
    ) {
        val httpRequest = request as HttpServletRequest
        val token = httpRequest.getHeader("Authorization")

        if (token != null && tokenService.verifyToken(token)) {
            val id = tokenService.getUid(token)
            val provider = tokenService.getProvider(token)
            val user = userService.getUserById(provider, id)
            val auth = getAuthentication(user)
            SecurityContextHolder.getContext().authentication = auth
        }

        chain.doFilter(request, response)
    }

    private fun getAuthentication(user: User): Authentication {
        return UsernamePasswordAuthenticationToken(user, "",
            listOf(SimpleGrantedAuthority("ROLE_USER")))
    }
}
