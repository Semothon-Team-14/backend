package semo.backend.dto

import java.time.LocalDateTime

data class ChatRoomDto(
    val id: Long,
    val name: String?,
    val directChat: Boolean,
    val participantUserIds: List<Long>,
    val createdDateTime: LocalDateTime,
    val updatedDateTime: LocalDateTime,
)
