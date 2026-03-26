package semo.backend.controller.response

import semo.backend.dto.ChatRoomDto

data class GetChatRoomsResponse(
    val chatRooms: List<ChatRoomDto>,
)
