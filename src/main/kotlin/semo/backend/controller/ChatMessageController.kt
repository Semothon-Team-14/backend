package semo.backend.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import semo.backend.controller.response.GetChatMessagesResponse
import semo.backend.facade.ChatMessageFacade
import semo.backend.security.UserId

@RestController
@RequestMapping("/chatrooms/{chatRoomId}/messages")
class ChatMessageController(
    private val chatMessageFacade: ChatMessageFacade,
) {
    @GetMapping
    fun getChatMessages(
        @UserId userId: Long,
        @PathVariable chatRoomId: Long,
    ): GetChatMessagesResponse {
        return GetChatMessagesResponse(
            messages = chatMessageFacade.getChatMessages(userId, chatRoomId),
        )
    }
}
