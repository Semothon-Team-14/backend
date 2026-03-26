package semo.backend.controller

import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import semo.backend.controller.request.SendChatMessageRequest
import semo.backend.dto.ChatMessageDto
import semo.backend.facade.ChatMessageFacade

@Controller
class ChatWebSocketController(
    private val chatMessageFacade: ChatMessageFacade,
    private val simpMessagingTemplate: SimpMessagingTemplate,
) {
    @MessageMapping("/chatrooms/{chatRoomId}/messages")
    fun sendMessage(
        @DestinationVariable chatRoomId: Long,
        request: SendChatMessageRequest,
        authentication: Authentication,
    ) {
        val userId = authentication.principal.toString().toLong()
        val message = chatMessageFacade.sendChatMessage(userId, chatRoomId, request)
        simpMessagingTemplate.convertAndSend("/topic/chatrooms/$chatRoomId", ChatMessagePayload(message))
    }

    data class ChatMessagePayload(
        val message: ChatMessageDto,
    )
}
