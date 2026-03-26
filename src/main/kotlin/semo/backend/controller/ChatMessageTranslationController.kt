package semo.backend.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import semo.backend.controller.request.UpsertChatMessageTranslationRequest
import semo.backend.controller.response.GetChatMessageTranslationResponse
import semo.backend.controller.response.UpsertChatMessageTranslationResponse
import semo.backend.facade.ChatMessageTranslationFacade
import semo.backend.security.UserId

@RestController
@RequestMapping("/chatmessages/{chatMessageId}/translation")
class ChatMessageTranslationController(
    private val chatMessageTranslationFacade: ChatMessageTranslationFacade,
) {
    @GetMapping
    fun getChatMessageTranslation(
        @UserId userId: Long,
        @PathVariable chatMessageId: Long,
    ): GetChatMessageTranslationResponse {
        return GetChatMessageTranslationResponse(
            translation = chatMessageTranslationFacade.getChatMessageTranslation(userId, chatMessageId),
        )
    }

    @PutMapping
    fun upsertChatMessageTranslation(
        @UserId userId: Long,
        @PathVariable chatMessageId: Long,
        @RequestBody request: UpsertChatMessageTranslationRequest,
    ): UpsertChatMessageTranslationResponse {
        return UpsertChatMessageTranslationResponse(
            translation = chatMessageTranslationFacade.upsertChatMessageTranslation(userId, chatMessageId, request),
        )
    }
}
