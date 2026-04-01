package semo.backend.websocket

import org.slf4j.LoggerFactory
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets

@Component
class StompLoggingChannelInterceptor : ChannelInterceptor {
    override fun preSend(
        message: Message<*>,
        channel: MessageChannel,
    ): Message<*> {
        val accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor::class.java)
            ?: return message

        val command = accessor.command ?: return message
        val direction = resolveDirection(channel.toString())
        val destination = accessor.destination ?: "-"
        val sessionId = accessor.sessionId ?: "-"
        val userId = accessor.user?.name ?: "-"
        val payload = maskSensitive(extractPayload(message.payload))

        log.info(
            "WS {} {} dest={} userId={} sessionId={} payload={}",
            direction,
            command.name,
            destination,
            userId,
            sessionId,
            payload,
        )

        return message
    }

    private fun resolveDirection(channel: String): String {
        return when {
            channel.contains("clientInboundChannel") -> "IN"
            channel.contains("clientOutboundChannel") -> "OUT"
            else -> "CHANNEL"
        }
    }

    private fun extractPayload(payload: Any?): String {
        if (payload == null) {
            return "-"
        }
        val raw = when (payload) {
            is ByteArray -> String(payload, StandardCharsets.UTF_8)
            else -> payload.toString()
        }.replace(Regex("\\s+"), " ").trim()
        if (raw.isEmpty()) {
            return "-"
        }
        return if (raw.length > MAX_PAYLOAD_LENGTH) {
            raw.take(MAX_PAYLOAD_LENGTH) + "...(truncated)"
        } else {
            raw
        }
    }

    private fun maskSensitive(raw: String): String {
        return raw
            .replace(Regex("(?i)\"password\"\\s*:\\s*\"[^\"]*\""), "\"password\":\"***\"")
            .replace(Regex("(?i)\"accessToken\"\\s*:\\s*\"[^\"]*\""), "\"accessToken\":\"***\"")
    }

    companion object {
        private val log = LoggerFactory.getLogger(StompLoggingChannelInterceptor::class.java)
        private const val MAX_PAYLOAD_LENGTH = 400
    }
}
