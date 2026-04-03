package semo.backend.dto

import java.time.LocalDateTime

data class ChatMessageDto(
    val id: Long,
    val chatRoomId: Long,
    val senderUserId: Long,
    val content: String,
    val translatedContent: String? = null,
    val createdDateTime: LocalDateTime,
)
