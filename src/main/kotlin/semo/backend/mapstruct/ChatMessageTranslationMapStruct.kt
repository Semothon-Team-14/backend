package semo.backend.mapstruct

import org.mapstruct.Mapper
import org.mapstruct.MappingConstants
import semo.backend.dto.ChatMessageTranslationDto
import semo.backend.entity.ChatMessageTranslation

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
interface ChatMessageTranslationMapStruct : GenericMapStruct<ChatMessageTranslation, ChatMessageTranslationDto> {
    override fun toDto(entity: ChatMessageTranslation): ChatMessageTranslationDto {
        return ChatMessageTranslationDto(
            id = entity.id,
            chatMessageId = entity.chatMessage.id,
            userId = entity.user.id,
            translatedContent = entity.translatedContent,
            createdDateTime = entity.createdDateTime,
            updatedDateTime = entity.updatedDateTime,
        )
    }
}
