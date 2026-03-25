package semo.backend.config

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import semo.backend.exception.BaseCustomException
import semo.backend.exception.auth.InvalidAccessTokenException

@Configuration
class SecurityExceptionConfig {
    @Bean
    fun authenticationEntryPoint(): AuthenticationEntryPoint {
        return AuthenticationEntryPoint { _: HttpServletRequest, response: HttpServletResponse, _: AuthenticationException? ->
            writeErrorResponse(response, InvalidAccessTokenException())
        }
    }

    companion object {
        fun writeErrorResponse(
            response: HttpServletResponse,
            exception: BaseCustomException,
        ) {
            response.status = exception.status.value()
            response.contentType = MediaType.APPLICATION_JSON_VALUE
            response.writer.write(
                """{"status":${exception.status.value()},"error":"${exception.status.name}","reason":"${exception.reason}"}""",
            )
        }
    }
}
