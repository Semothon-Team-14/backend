package semo.backend.controller.response

import java.time.LocalDateTime

data class MarkChatRoomReadResponse(
    val chatRoomId: Long,
    val lastReadDateTime: LocalDateTime,
)
