package semo.backend.exception.chat

import org.springframework.http.HttpStatus
import semo.backend.exception.BaseCustomException

class InvalidChatRoomInitializationException(
) : BaseCustomException(
    status = HttpStatus.BAD_REQUEST,
    reasonTemplate = "Chat room must include at least one participant other than the current user",
)
