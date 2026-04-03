package semo.backend.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import semo.backend.controller.request.SendChatMessageRequest
import semo.backend.dto.ChatMessageDeliveryDto

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
            val detectedLanguageCode = try {
                openAiTranslationService.detectLanguageCode(message.content)
            } catch (exception: Exception) {
                log.warn(
                    "CHAT LANGUAGE DETECTION FAILED chatMessageId={} senderUserId={} reason={}",
                    message.id,
                    userId,
                    exception.message ?: exception::class.simpleName ?: "unknown",
                )
                null
            }

            chatRoom.participants
                .asSequence()
                .filter { it.user.id != userId }
                .mapNotNull { participant ->
                    val recipientNationality = participant.user.nationality ?: return@mapNotNull null
                    val recipientPrimaryLanguageCode = openAiTranslationService
                        .primaryLanguageCodeByCountryCode(recipientNationality.countryCode)

                    if (
                        recipientNationality.id == senderNationality.id ||
                        (detectedLanguageCode != null &&
                            recipientPrimaryLanguageCode != null &&
                            detectedLanguageCode == recipientPrimaryLanguageCode)
                    ) {
                        return@mapNotNull null
                    }

                    try {
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
                    } catch (exception: Exception) {
                        log.warn(
                            "CHAT TRANSLATION SKIPPED chatMessageId={} senderUserId={} recipientUserId={} reason={}",
                            message.id,
                            userId,
                            participant.user.id,
                            exception.message ?: exception::class.simpleName ?: "unknown",
                        )
                        null
                    }
                }
                .toList()
        }

        return ChatMessageDeliveryDto(
            message = message,
            translations = translations,
        )
    }

    companion object {
        private val log = LoggerFactory.getLogger(ChatRealtimeMessageService::class.java)
    }
}
