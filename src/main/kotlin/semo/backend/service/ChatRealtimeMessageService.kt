package semo.backend.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import semo.backend.controller.request.SendChatMessageRequest
import semo.backend.dto.ChatMessageDeliveryDto
import semo.backend.dto.ChatMessageTranslationDto

@Service
@Transactional(readOnly = true)
class ChatRealtimeMessageService(
    private val chatMessageService: ChatMessageService,
    private val chatRoomService: ChatRoomService,
    private val chatMessageTranslationService: ChatMessageTranslationService,
    private val openAiTranslationService: OpenAiTranslationService,
) {
    @Transactional
    fun sendChatMessage(userId: Long, chatRoomId: Long, request: SendChatMessageRequest): ChatMessageDeliveryDto {
        val message = chatMessageService.sendChatMessage(userId, chatRoomId, request)
        val chatRoom = chatRoomService.findChatRoomForUser(userId, chatRoomId)
        val senderNationality = chatRoom.participants
            .firstOrNull { it.user.id == userId }
            ?.user
            ?.nationality

        val translations = if (senderNationality == null) {
            emptyList()
        } else {
            chatRoom.participants
                .asSequence()
                .filter { it.user.id != userId }
                .mapNotNull { participant ->
                    val recipientNationality = participant.user.nationality ?: return@mapNotNull null
                    if (recipientNationality.id == senderNationality.id) {
                        return@mapNotNull null
                    }

                    val translatedContent = openAiTranslationService.translateText(
                        originalContent = message.content,
                        sourceCountryName = senderNationality.countryNameEnglish,
                        targetCountryName = recipientNationality.countryNameEnglish,
                    )
                    chatMessageTranslationService.upsertChatMessageTranslation(
                        userId = participant.user.id,
                        chatMessageId = message.id,
                        translatedContent = translatedContent,
                    )
                }
                .toList()
        }

        return ChatMessageDeliveryDto(
            message = message,
            translations = translations,
        )
    }
}
