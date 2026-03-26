package semo.backend.facade

import org.springframework.stereotype.Service
import semo.backend.controller.request.SendChatMessageRequest
import semo.backend.dto.ChatMessageDeliveryDto
import semo.backend.dto.ChatMessageDto
import semo.backend.service.ChatRealtimeMessageService
import semo.backend.service.ChatMessageService

@Service
class ChatMessageFacade(
    private val chatMessageService: ChatMessageService,
    private val chatRealtimeMessageService: ChatRealtimeMessageService,
) {
    fun getChatMessages(userId: Long, chatRoomId: Long): List<ChatMessageDto> {
        return chatMessageService.getChatMessages(userId, chatRoomId)
    }

    fun sendChatMessage(userId: Long, chatRoomId: Long, request: SendChatMessageRequest): ChatMessageDeliveryDto {
        return chatRealtimeMessageService.sendChatMessage(userId, chatRoomId, request)
    }
}
