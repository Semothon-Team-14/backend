package semo.backend.exception.chat

import org.springframework.http.HttpStatus
import semo.backend.exception.BaseCustomException

class ChatRoomNotFoundException(
    chatRoomId: Long,
) : BaseCustomException(
    status = HttpStatus.NOT_FOUND,
    reasonTemplate = "Chat room not found for id={chatRoomId}",
    reasonVariables = mapOf("chatRoomId" to chatRoomId),
)
