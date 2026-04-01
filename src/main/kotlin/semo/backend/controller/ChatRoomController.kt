package semo.backend.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import semo.backend.controller.request.InitializeChatRoomRequest
import semo.backend.controller.response.GetChatRoomResponse
import semo.backend.controller.response.GetChatRoomsResponse
import semo.backend.controller.response.InitializeChatRoomResponse
import semo.backend.controller.response.JoinMingleChatRoomResponse
import semo.backend.facade.ChatRoomFacade
import semo.backend.security.UserId

@RestController
@RequestMapping("/chatrooms")
class ChatRoomController(
    private val chatRoomFacade: ChatRoomFacade,
) {
    @GetMapping
    fun getChatRooms(
        @UserId userId: Long,
    ): GetChatRoomsResponse {
        return GetChatRoomsResponse(
            chatRooms = chatRoomFacade.getChatRooms(userId),
        )
    }

    @GetMapping("/{chatRoomId}")
    fun getChatRoom(
        @UserId userId: Long,
        @PathVariable chatRoomId: Long,
    ): GetChatRoomResponse {
        return GetChatRoomResponse(
            chatRoom = chatRoomFacade.getChatRoom(userId, chatRoomId),
        )
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun initializeChatRoom(
        @UserId userId: Long,
        @RequestBody request: InitializeChatRoomRequest,
    ): InitializeChatRoomResponse {
        return InitializeChatRoomResponse(
            chatRoom = chatRoomFacade.initializeChatRoom(userId, request),
        )
    }

    @PostMapping("/mingles/{mingleId}/join")
    fun joinMingleChatRoom(
        @UserId userId: Long,
        @PathVariable mingleId: Long,
    ): JoinMingleChatRoomResponse {
        return JoinMingleChatRoomResponse(
            chatRoom = chatRoomFacade.joinMingleChatRoom(userId, mingleId),
        )
    }
}
