package semo.backend.facade

import org.springframework.stereotype.Service
import semo.backend.controller.request.InitializeChatRoomRequest
import semo.backend.dto.ChatRoomDto
import semo.backend.service.ChatRoomService
import java.time.LocalDateTime

@Service
class ChatRoomFacade(
    private val chatRoomService: ChatRoomService,
) {
    fun getChatRooms(userId: Long): List<ChatRoomDto> {
        return chatRoomService.getChatRooms(userId)
    }

    fun getChatRoom(userId: Long, chatRoomId: Long): ChatRoomDto {
        return chatRoomService.getChatRoom(userId, chatRoomId)
    }

    fun initializeChatRoom(userId: Long, request: InitializeChatRoomRequest): ChatRoomDto {
        return chatRoomService.initializeChatRoom(userId, request)
    }

    fun joinMingleChatRoom(userId: Long, mingleId: Long): ChatRoomDto {
        return chatRoomService.joinMingleChatRoom(userId, mingleId)
    }

    fun markChatRoomAsRead(userId: Long, chatRoomId: Long): LocalDateTime {
        return chatRoomService.markChatRoomAsRead(userId, chatRoomId)
    }
}
