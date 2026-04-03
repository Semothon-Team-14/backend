package semo.backend.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import semo.backend.config.SecurityExceptionConfig
import semo.backend.exception.BaseCustomException
import semo.backend.exception.auth.AccessTokenMissingException

@Component
class AccessTokenAuthenticationFilter(
    private val accessTokenService: AccessTokenService,
) : OncePerRequestFilter() {
    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.servletPath
        return path == "/auth/login" ||
            (path == "/users" && request.method == "POST") ||
            (request.method == "GET" && (path == "/nationalities" || path.startsWith("/nationalities/"))) ||
            (request.method == "GET" && (path == "/keywords" || path.startsWith("/keywords/"))) ||
            path == "/swagger-ui.html" ||
            path.startsWith("/swagger-ui/") ||
            path == "/api-docs" ||
            path.startsWith("/api-docs/") ||
            path == "/ws-chat" ||
            path.startsWith("/ws-chat/")
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        try {
            val token = request.getHeader(ACCESS_TOKEN_HEADER)?.trim().orEmpty()
            if (token.isBlank()) {
                throw AccessTokenMissingException()
            }

            val userId = accessTokenService.extractUserId(token)
            val authentication = UsernamePasswordAuthenticationToken.authenticated(userId, token, emptyList())
            SecurityContextHolder.getContext().authentication = authentication

            filterChain.doFilter(request, response)
        } catch (exception: BaseCustomException) {
            SecurityContextHolder.clearContext()
            SecurityExceptionConfig.writeErrorResponse(response, exception)
        }
    }

    companion object {
        const val ACCESS_TOKEN_HEADER = "accessToken"
    }
}
