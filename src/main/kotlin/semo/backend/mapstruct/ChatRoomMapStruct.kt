package semo.backend.mapstruct

import org.mapstruct.Mapper
import org.mapstruct.MappingConstants
import semo.backend.dto.ChatRoomDto
import semo.backend.entity.ChatRoom

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
interface ChatRoomMapStruct : GenericMapStruct<ChatRoom, ChatRoomDto> {
    override fun toDto(entity: ChatRoom): ChatRoomDto {
        return ChatRoomDto(
            id = entity.id,
            name = entity.name,
            directChat = entity.directChat,
            participantUserIds = entity.participants.map { it.user.id }.sorted(),
            createdDateTime = entity.createdDateTime,
            updatedDateTime = entity.updatedDateTime,
        )
    }
}
