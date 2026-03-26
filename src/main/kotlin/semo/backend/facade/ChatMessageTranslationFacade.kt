package semo.backend.facade

import org.springframework.stereotype.Service
import semo.backend.controller.request.UpsertChatMessageTranslationRequest
import semo.backend.dto.ChatMessageTranslationDto
import semo.backend.service.ChatMessageTranslationService

@Service
class ChatMessageTranslationFacade(
    private val chatMessageTranslationService: ChatMessageTranslationService,
) {
    fun getChatMessageTranslation(userId: Long, chatMessageId: Long): ChatMessageTranslationDto {
        return chatMessageTranslationService.getChatMessageTranslation(userId, chatMessageId)
    }

    fun upsertChatMessageTranslation(
        userId: Long,
        chatMessageId: Long,
        request: UpsertChatMessageTranslationRequest,
    ): ChatMessageTranslationDto {
        return chatMessageTranslationService.upsertChatMessageTranslation(userId, chatMessageId, request)
    }
}
