package semo.backend.exception.chat

import org.springframework.http.HttpStatus
import semo.backend.exception.BaseCustomException

class MingleChatRoomNotFoundException(
    mingleId: Long,
) : BaseCustomException(
    status = HttpStatus.NOT_FOUND,
    reasonTemplate = "Chat room not found for mingle id={mingleId}",
    reasonVariables = mapOf("mingleId" to mingleId),
)
