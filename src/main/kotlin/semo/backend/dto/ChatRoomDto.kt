package semo.backend.dto

import java.time.LocalDateTime

data class ChatRoomDto(
    val id: Long,
    val name: String?,
    val directChat: Boolean,
    val mingleId: Long?,
    val participantUserIds: List<Long>,
    val otherParticipantLocal: Boolean? = null,
    val otherParticipantTraveler: Boolean? = null,
    val unreadMessageCount: Long = 0,
    val createdDateTime: LocalDateTime,
    val updatedDateTime: LocalDateTime,
)
