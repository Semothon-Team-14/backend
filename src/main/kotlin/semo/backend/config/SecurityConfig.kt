package semo.backend.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.http.HttpMethod
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import semo.backend.security.AccessTokenAuthenticationFilter

@Configuration
class SecurityConfig(
    private val accessTokenAuthenticationFilter: AccessTokenAuthenticationFilter,
    private val authenticationEntryPoint: AuthenticationEntryPoint,
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { csrf -> csrf.disable() }
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .exceptionHandling { exception ->
                exception.authenticationEntryPoint(authenticationEntryPoint)
            }
            .authorizeHttpRequests { authorize ->
                authorize
                    .requestMatchers(
                        "/auth/login",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/api-docs",
                        "/api-docs/**",
                        "/error",
                    )
                    .permitAll()
                    .requestMatchers(HttpMethod.POST, "/users").permitAll()
                    .anyRequest()
                    .authenticated()
            }
            .addFilterBefore(accessTokenAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}
