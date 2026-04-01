package semo.backend.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import semo.backend.controller.request.UpsertChatMessageTranslationRequest
import semo.backend.dto.ChatMessageTranslationDto
import semo.backend.entity.ChatMessageTranslation
import semo.backend.entity.User
import semo.backend.exception.chat.ChatMessageTranslationNotFoundException
import semo.backend.exception.chat.EmptyChatTranslationContentException
import semo.backend.mapstruct.ChatMessageTranslationMapStruct
import semo.backend.repository.jpa.ChatMessageTranslationRepository
import semo.backend.repository.jpa.UserRepository
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
class ChatMessageTranslationService(
    private val chatMessageService: ChatMessageService,
    private val chatMessageTranslationRepository: ChatMessageTranslationRepository,
    private val userRepository: UserRepository,
    private val chatMessageTranslationMapStruct: ChatMessageTranslationMapStruct,
) {
    fun getChatMessageTranslation(userId: Long, chatMessageId: Long): ChatMessageTranslationDto {
        val translation = findTranslationByChatMessageIdAndUserId(userId, chatMessageId)
        return chatMessageTranslationMapStruct.toDto(translation)
    }

    @Transactional
    fun upsertChatMessageTranslation(
        userId: Long,
        chatMessageId: Long,
        request: UpsertChatMessageTranslationRequest,
    ): ChatMessageTranslationDto {
        return upsertChatMessageTranslation(userId, chatMessageId, request.translatedContent)
    }

    @Transactional
    fun upsertChatMessageTranslation(
        userId: Long,
        chatMessageId: Long,
        translatedContent: String,
    ): ChatMessageTranslationDto {
        val normalizedTranslatedContent = translatedContent.trim()
        if (normalizedTranslatedContent.isBlank()) {
            throw EmptyChatTranslationContentException()
        }

        val chatMessage = chatMessageService.findChatMessageForParticipant(userId, chatMessageId)
        val user = findUserById(userId)
        val now = LocalDateTime.now()
        val translation = chatMessageTranslationRepository.findByChatMessageIdAndUserId(chatMessageId, userId)
            ?.apply {
                this.translatedContent = normalizedTranslatedContent
                this.updatedDateTime = now
            }
            ?: ChatMessageTranslation(
                chatMessage = chatMessage,
                user = user,
                translatedContent = normalizedTranslatedContent,
                createdDateTime = now,
                updatedDateTime = now,
            )

        return chatMessageTranslationMapStruct.toDto(chatMessageTranslationRepository.save(translation))
    }

    private fun findTranslationByChatMessageIdAndUserId(
        userId: Long,
        chatMessageId: Long,
    ): ChatMessageTranslation {
        chatMessageService.findChatMessageForParticipant(userId, chatMessageId)
        return chatMessageTranslationRepository.findByChatMessageIdAndUserId(chatMessageId, userId)
            ?: throw ChatMessageTranslationNotFoundException(chatMessageId, userId)
    }

    private fun findUserById(userId: Long): User {
        return userRepository.findById(userId)
            .orElseThrow { semo.backend.exception.user.UserNotFoundException(userId) }
    }
}
