package semo.backend.controller.response

import semo.backend.dto.ChatMessageTranslationDto

data class GetChatMessageTranslationResponse(
    val translation: ChatMessageTranslationDto,
)
