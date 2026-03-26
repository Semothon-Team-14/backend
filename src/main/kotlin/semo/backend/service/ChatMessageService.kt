package semo.backend.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import semo.backend.controller.request.SendChatMessageRequest
import semo.backend.dto.ChatMessageDto
import semo.backend.entity.ChatMessage
import semo.backend.entity.User
import semo.backend.exception.chat.EmptyChatMessageException
import semo.backend.exception.chat.ChatMessageNotFoundException
import semo.backend.exception.user.UserNotFoundException
import semo.backend.mapstruct.ChatMessageMapStruct
import semo.backend.repository.jpa.ChatMessageRepository
import semo.backend.repository.jpa.ChatParticipantRepository
import semo.backend.repository.jpa.UserRepository
import java.time.LocalDateTime

@Service
class ChatMessageService(
    private val chatRoomService: ChatRoomService,
    private val chatMessageRepository: ChatMessageRepository,
    private val chatParticipantRepository: ChatParticipantRepository,
    private val userRepository: UserRepository,
    private val chatMessageMapStruct: ChatMessageMapStruct,
) {
    fun getChatMessages(userId: Long, chatRoomId: Long): List<ChatMessageDto> {
        chatRoomService.validateParticipation(userId, chatRoomId)
        return chatMessageMapStruct.toDtos(chatMessageRepository.findAllByChatRoomIdOrderByCreatedDateTimeAsc(chatRoomId))
    }

    @Transactional
    fun sendChatMessage(userId: Long, chatRoomId: Long, request: SendChatMessageRequest): ChatMessageDto {
        val content = request.content.trim()
        if (content.isBlank()) {
            throw EmptyChatMessageException()
        }

        val chatRoom = chatRoomService.findChatRoomForUser(userId, chatRoomId)
        val now = LocalDateTime.now()
        val chatMessage = ChatMessage(
            chatRoom = chatRoom,
            senderUser = findUserById(userId),
            content = content,
            createdDateTime = now,
        )
        chatRoom.updatedDateTime = now
        val savedMessage = chatMessageRepository.save(chatMessage)

        return chatMessageMapStruct.toDto(savedMessage)
    }

    fun findChatMessageForParticipant(userId: Long, chatMessageId: Long): ChatMessage {
        val chatMessage = chatMessageRepository.findById(chatMessageId)
            .orElseThrow { ChatMessageNotFoundException(chatMessageId) }
        if (!chatParticipantRepository.existsByChatRoomIdAndUserId(chatMessage.chatRoom.id, userId)) {
            throw semo.backend.exception.chat.ChatRoomAccessDeniedException(chatMessage.chatRoom.id, userId)
        }
        return chatMessage
    }

    private fun findUserById(userId: Long): User {
        return userRepository.findById(userId)
            .orElseThrow { UserNotFoundException(userId) }
    }
}
