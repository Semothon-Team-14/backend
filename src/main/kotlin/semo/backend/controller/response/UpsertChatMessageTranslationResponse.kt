package semo.backend.controller.response

import semo.backend.dto.ChatMessageTranslationDto

data class UpsertChatMessageTranslationResponse(
    val translation: ChatMessageTranslationDto,
)
