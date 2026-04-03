package semo.backend.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import semo.backend.config.argument.OptionalRequestBody
import semo.backend.controller.request.CreateMingleRequest
import semo.backend.controller.request.UpdateMingleRequest
import semo.backend.controller.response.CreateMingleResponse
import semo.backend.controller.response.CreateMinglerResponse
import semo.backend.controller.response.DeleteMingleResponse
import semo.backend.controller.response.DeleteMinglerResponse
import semo.backend.controller.response.GetMingleResponse
import semo.backend.controller.response.GetMinglersResponse
import semo.backend.controller.response.GetMinglesResponse
import semo.backend.controller.response.UpdateMingleResponse
import semo.backend.facade.ChatRoomFacade
import semo.backend.facade.MingleFacade
import semo.backend.facade.MinglerFacade
import semo.backend.security.UserId

@RestController
@RequestMapping("/mingles")
class MingleController(
    private val mingleFacade: MingleFacade,
    private val minglerFacade: MinglerFacade,
    private val chatRoomFacade: ChatRoomFacade,
) {
    @GetMapping
    fun getMingles(
        @RequestParam(required = false) cityId: Long?,
    ): GetMinglesResponse {
        return GetMinglesResponse(
            mingles = mingleFacade.getMingles(cityId),
        )
    }

    @GetMapping("/{mingleId}")
    fun getMingle(
        @PathVariable mingleId: Long,
    ): GetMingleResponse {
        return GetMingleResponse(
            mingle = mingleFacade.getMingle(mingleId),
        )
    }

    @GetMapping("/{mingleId}/minglers")
    fun getMingleMinglers(
        @PathVariable mingleId: Long,
    ): GetMinglersResponse {
        return GetMinglersResponse(
            minglers = minglerFacade.getMinglersByMingle(mingleId),
        )
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createMingle(
        @UserId userId: Long,
        @RequestBody request: CreateMingleRequest,
    ): CreateMingleResponse {
        return CreateMingleResponse(
            mingle = mingleFacade.createMingle(userId, request),
        )
    }

    @PutMapping("/{mingleId}")
    fun updateMingle(
        @PathVariable mingleId: Long,
        @OptionalRequestBody request: UpdateMingleRequest,
    ): UpdateMingleResponse {
        return UpdateMingleResponse(
            mingle = mingleFacade.updateMingle(mingleId, request),
        )
    }

    @DeleteMapping("/{mingleId}")
    fun deleteMingle(
        @PathVariable mingleId: Long,
    ): DeleteMingleResponse {
        return DeleteMingleResponse(
            deletedMingleId = mingleFacade.deleteMingle(mingleId),
        )
    }

    @PostMapping("/{mingleId}/minglers")
    @ResponseStatus(HttpStatus.CREATED)
    fun joinMingle(
        @UserId userId: Long,
        @PathVariable mingleId: Long,
    ): CreateMinglerResponse {
        val mingler = minglerFacade.joinMingle(userId, mingleId)
        chatRoomFacade.ensureMingleChatRoomAndJoin(userId, mingleId)
        return CreateMinglerResponse(
            mingler = mingler,
        )
    }

    @DeleteMapping("/{mingleId}/minglers/me")
    fun leaveMingle(
        @UserId userId: Long,
        @PathVariable mingleId: Long,
    ): DeleteMinglerResponse {
        chatRoomFacade.leaveMingleChatRoom(userId, mingleId)
        return DeleteMinglerResponse(
            deletedMinglerId = minglerFacade.leaveMingle(userId, mingleId),
        )
    }
}
