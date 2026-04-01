package semo.backend.websocket

import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Component
import org.slf4j.LoggerFactory
import semo.backend.exception.auth.AccessTokenMissingException
import semo.backend.security.AccessTokenService

@Component
class StompAuthChannelInterceptor(
    private val accessTokenService: AccessTokenService,
) : ChannelInterceptor {
    override fun preSend(
        message: Message<*>,
        channel: MessageChannel,
    ): Message<*> {
        val accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor::class.java)
            ?: return message

        if (accessor.command == StompCommand.CONNECT) {
            val token = accessor.getFirstNativeHeader(ACCESS_TOKEN_HEADER)?.trim().orEmpty()
            if (token.isBlank()) {
                log.warn(
                    "WS AUTH FAIL CONNECT missing accessToken sessionId={}",
                    accessor.sessionId ?: "-",
                )
                throw AccessTokenMissingException()
            }

            try {
                val userId = accessTokenService.extractUserId(token)
                accessor.user = UsernamePasswordAuthenticationToken.authenticated(userId, token, emptyList())
                log.info(
                    "WS AUTH OK CONNECT userId={} sessionId={}",
                    userId,
                    accessor.sessionId ?: "-",
                )
            } catch (exception: RuntimeException) {
                log.warn(
                    "WS AUTH FAIL CONNECT invalid token sessionId={} reason={}",
                    accessor.sessionId ?: "-",
                    exception.message ?: exception.javaClass.simpleName,
                )
                throw exception
            }
        }

        return message
    }

    companion object {
        private val log = LoggerFactory.getLogger(StompAuthChannelInterceptor::class.java)
        private const val ACCESS_TOKEN_HEADER = "accessToken"
    }
}
