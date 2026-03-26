package semo.backend.mapstruct

import org.mapstruct.Mapper
import org.mapstruct.MappingConstants
import semo.backend.dto.ChatMessageDto
import semo.backend.entity.ChatMessage

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
interface ChatMessageMapStruct : GenericMapStruct<ChatMessage, ChatMessageDto> {
    override fun toDto(entity: ChatMessage): ChatMessageDto {
        return ChatMessageDto(
            id = entity.id,
            chatRoomId = entity.chatRoom.id,
            senderUserId = entity.senderUser.id,
            content = entity.content,
            createdDateTime = entity.createdDateTime,
        )
    }
}
