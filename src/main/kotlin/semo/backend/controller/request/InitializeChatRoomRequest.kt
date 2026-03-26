package semo.backend.controller.request

data class InitializeChatRoomRequest(
    val participantUserIds: List<Long>,
    val name: String? = null,
)
