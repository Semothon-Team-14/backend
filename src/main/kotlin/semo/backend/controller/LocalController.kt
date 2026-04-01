package semo.backend.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import semo.backend.config.argument.OptionalRequestBody
import semo.backend.controller.request.CreateLocalRequest
import semo.backend.controller.request.UpdateLocalRequest
import semo.backend.controller.response.CreateLocalResponse
import semo.backend.controller.response.DeleteLocalResponse
import semo.backend.controller.response.GetLocalResponse
import semo.backend.controller.response.GetLocalsResponse
import semo.backend.controller.response.UpdateLocalResponse
import semo.backend.facade.LocalFacade
import semo.backend.security.UserId

@RestController
@RequestMapping("/locals")
class LocalController(
    private val localFacade: LocalFacade,
) {
    @GetMapping
    fun getLocals(
        @UserId userId: Long,
    ): GetLocalsResponse {
        return GetLocalsResponse(
            locals = localFacade.getLocals(userId),
        )
    }

    @GetMapping("/{localId}")
    fun getLocal(
        @UserId userId: Long,
        @PathVariable localId: Long,
    ): GetLocalResponse {
        return GetLocalResponse(
            local = localFacade.getLocal(userId, localId),
        )
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createLocal(
        @UserId userId: Long,
        @RequestBody request: CreateLocalRequest,
    ): CreateLocalResponse {
        return CreateLocalResponse(
            local = localFacade.createLocal(userId, request),
        )
    }

    @PutMapping("/{localId}")
    fun updateLocal(
        @UserId userId: Long,
        @PathVariable localId: Long,
        @OptionalRequestBody request: UpdateLocalRequest,
    ): UpdateLocalResponse {
        return UpdateLocalResponse(
            local = localFacade.updateLocal(userId, localId, request),
        )
    }

    @DeleteMapping("/{localId}")
    fun deleteLocal(
        @UserId userId: Long,
        @PathVariable localId: Long,
    ): DeleteLocalResponse {
        return DeleteLocalResponse(
            deletedLocalId = localFacade.deleteLocal(userId, localId),
        )
    }
}
