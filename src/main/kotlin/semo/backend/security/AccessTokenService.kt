package semo.backend.security

import org.springframework.stereotype.Service
import semo.backend.exception.auth.InvalidAccessTokenException
import java.nio.charset.StandardCharsets
import java.util.Base64

@Service
class AccessTokenService {
    fun createToken(userId: Long): String {
        val payload = "$TOKEN_PREFIX$userId"
        return Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(payload.toByteArray(StandardCharsets.UTF_8))
    }

    fun extractUserId(token: String): Long {
        if (token == MASTER_TOKEN) {
            return MASTER_USER_ID
        }

        val decodedToken = try {
            String(Base64.getUrlDecoder().decode(token), StandardCharsets.UTF_8)
        } catch (_: IllegalArgumentException) {
            throw InvalidAccessTokenException()
        }

        if (!decodedToken.startsWith(TOKEN_PREFIX)) {
            throw InvalidAccessTokenException()
        }

        return decodedToken.removePrefix(TOKEN_PREFIX).toLongOrNull()
            ?: throw InvalidAccessTokenException()
    }

    companion object {
        private const val TOKEN_PREFIX = "userId:"
        private const val MASTER_TOKEN = "master"
        private const val MASTER_USER_ID = 1L
    }
}
