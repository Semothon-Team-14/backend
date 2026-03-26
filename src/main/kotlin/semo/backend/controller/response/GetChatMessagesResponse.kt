package semo.backend.controller.response

import semo.backend.dto.ChatMessageDto

data class GetChatMessagesResponse(
    val messages: List<ChatMessageDto>,
)
