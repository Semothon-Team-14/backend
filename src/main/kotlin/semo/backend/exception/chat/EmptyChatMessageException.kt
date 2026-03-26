package semo.backend.exception.chat

import org.springframework.http.HttpStatus
import semo.backend.exception.BaseCustomException

class EmptyChatMessageException(
) : BaseCustomException(
    status = HttpStatus.BAD_REQUEST,
    reasonTemplate = "Chat message content must not be blank",
)
