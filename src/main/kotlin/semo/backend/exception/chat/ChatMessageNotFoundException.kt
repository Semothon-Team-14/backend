package semo.backend.exception.chat

import org.springframework.http.HttpStatus
import semo.backend.exception.BaseCustomException

class ChatMessageNotFoundException(
    chatMessageId: Long,
) : BaseCustomException(
    status = HttpStatus.NOT_FOUND,
    reasonTemplate = "Chat message not found for id={chatMessageId}",
    reasonVariables = mapOf("chatMessageId" to chatMessageId),
)
