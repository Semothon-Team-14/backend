package semo.backend.facade

import org.springframework.stereotype.Service
import semo.backend.controller.request.SendChatMessageRequest
import semo.backend.dto.ChatMessageDto
import semo.backend.service.ChatMessageService

@Service
class ChatMessageFacade(
    private val chatMessageService: ChatMessageService,
) {
    fun getChatMessages(userId: Long, chatRoomId: Long): List<ChatMessageDto> {
        return chatMessageService.getChatMessages(userId, chatRoomId)
    }

    fun sendChatMessage(userId: Long, chatRoomId: Long, request: SendChatMessageRequest): ChatMessageDto {
        return chatMessageService.sendChatMessage(userId, chatRoomId, request)
    }
}
