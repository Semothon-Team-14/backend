package semo.backend.dto

import java.time.LocalDateTime

data class ChatMessageTranslationDto(
    val id: Long,
    val chatMessageId: Long,
    val userId: Long,
    val translatedContent: String,
    val createdDateTime: LocalDateTime,
    val updatedDateTime: LocalDateTime,
)
