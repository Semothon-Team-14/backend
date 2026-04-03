package semo.backend.logging

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

@Component
class ApiRequestResponseLoggingFilter : OncePerRequestFilter() {
    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.requestURI ?: return true
        val isGet = request.method.equals("GET", ignoreCase = true)
        return path.startsWith("/ws-chat") ||
            path.startsWith("/swagger-ui") ||
            path.startsWith("/api-docs") ||
            path.startsWith("/actuator") ||
            (isGet && (path == "/cities" || path.startsWith("/cities/"))) ||
            (isGet && (path == "/nationalities" || path.startsWith("/nationalities/"))) ||
            isNoisyPlaceApiPath(path)
    }

    private fun isNoisyPlaceApiPath(path: String): Boolean {
        return path.startsWith("/restaurants/cities/") ||
            path.startsWith("/restaurants/") && path.endsWith("/images") ||
            path.startsWith("/cafes/cities/") ||
            path.startsWith("/cafes/") && path.endsWith("/images")
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val wrappedRequest = ContentCachingRequestWrapper(request, MAX_BODY_LENGTH)
        val wrappedResponse = ContentCachingResponseWrapper(response)
        val startedAt = System.currentTimeMillis()

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse)
        } finally {
            val durationMs = System.currentTimeMillis() - startedAt
            val requestLine = "${request.method} ${request.requestURI}${request.queryString?.let { "?$it" } ?: ""}"
            val requestBody = extractBody(wrappedRequest.contentAsByteArray, wrappedRequest.characterEncoding)
            val responseBody = extractBody(wrappedResponse.contentAsByteArray, wrappedResponse.characterEncoding)

            log.info("REQ {} body={}", requestLine, requestBody)
            log.info(
                "RES {} status={} durationMs={} body={}",
                requestLine,
                wrappedResponse.status,
                durationMs,
                responseBody,
            )

            wrappedResponse.copyBodyToResponse()
        }
    }

    private fun extractBody(
        content: ByteArray,
        encoding: String?,
    ): String {
        if (content.isEmpty()) {
            return "-"
        }
        val charset = encoding?.let { runCatching { Charset.forName(it) }.getOrNull() } ?: StandardCharsets.UTF_8
        val raw = String(content, charset).replace(Regex("\\s+"), " ").trim()
        val masked = raw
            .replace(Regex("(?i)\"password\"\\s*:\\s*\"[^\"]*\""), "\"password\":\"***\"")
            .replace(Regex("(?i)\"accessToken\"\\s*:\\s*\"[^\"]*\""), "\"accessToken\":\"***\"")
        return if (masked.length > MAX_BODY_LENGTH) {
            masked.take(MAX_BODY_LENGTH) + "...(truncated)"
        } else {
            masked
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(ApiRequestResponseLoggingFilter::class.java)
        private const val MAX_BODY_LENGTH = 600
    }
}
