package semo.backend.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfig {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { authorize ->
                authorize
                    .requestMatchers(
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/api-docs",
                        "/api-docs/**",
                    )
                    .permitAll()
                    .anyRequest()
                    .authenticated()
            }
            .httpBasic(Customizer.withDefaults())
            .formLogin(Customizer.withDefaults())

        return http.build()
    }
}
