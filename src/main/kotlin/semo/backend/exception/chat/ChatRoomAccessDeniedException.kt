package semo.backend.exception.chat

import org.springframework.http.HttpStatus
import semo.backend.exception.BaseCustomException

class ChatRoomAccessDeniedException(
    chatRoomId: Long,
    userId: Long,
) : BaseCustomException(
    status = HttpStatus.FORBIDDEN,
    reasonTemplate = "User {userId} is not a participant of chat room {chatRoomId}",
    reasonVariables = mapOf(
        "chatRoomId" to chatRoomId,
        "userId" to userId,
    ),
)
