package com.blackcowmoo.moomark.auth.configuration

import com.blackcowmoo.moomark.auth.configuration.oauth2.JwtAuthFilter
import com.blackcowmoo.moomark.auth.configuration.oauth2.PassportFilter
import com.blackcowmoo.moomark.auth.service.PassportService
import com.blackcowmoo.moomark.auth.service.TokenService
import com.blackcowmoo.moomark.auth.service.UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val tokenService: TokenService,
    private val userService: UserService,
    private val passportService: PassportService
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .sessionManagement { sessionManagement ->
                sessionManagement.sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }
            .csrf { csrf -> csrf.disable() }
            .formLogin { formLogin -> formLogin.disable() }
            .httpBasic { httpBasic -> httpBasic.disable() }
            .logout { logout -> logout.disable() }
            .exceptionHandling { exceptionHandling ->
                exceptionHandling.authenticationEntryPoint = HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
            }
            .authorizeRequests { authorizeRequests ->
                authorizeRequests
                    .antMatchers("/api/v1/oauth2/refresh").permitAll()
                    .antMatchers("/api/v1/oauth2/google").permitAll()
                    .antMatchers("/api/v1/user/{provider}/{userId}").permitAll()
                    .antMatchers("/api/v1/passport/verify").permitAll()
                    .antMatchers("/api/v1/passport/verify/public").permitAll()
                    .antMatchers("/actuator/health").permitAll()
                    .anyRequest().authenticated()
            }

        http.addFilterBefore(JwtAuthFilter(tokenService, userService), UsernamePasswordAuthenticationFilter::class.java)
        http.addFilterBefore(PassportFilter(passportService), UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}