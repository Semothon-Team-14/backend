package semo.backend.dto

data class ChatMessageDeliveryDto(
    val message: ChatMessageDto,
    val translations: List<ChatMessageTranslationDto>,
)
