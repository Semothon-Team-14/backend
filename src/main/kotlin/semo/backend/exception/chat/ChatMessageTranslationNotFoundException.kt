package semo.backend.exception.chat

import org.springframework.http.HttpStatus
import semo.backend.exception.BaseCustomException

class ChatMessageTranslationNotFoundException(
    chatMessageId: Long,
    userId: Long,
) : BaseCustomException(
    status = HttpStatus.NOT_FOUND,
    reasonTemplate = "Chat message translation not found for chatMessageId={chatMessageId}, userId={userId}",
    reasonVariables = mapOf(
        "chatMessageId" to chatMessageId,
        "userId" to userId,
    ),
)
