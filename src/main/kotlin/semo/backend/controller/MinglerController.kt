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
import semo.backend.controller.request.CreateMinglerRequest
import semo.backend.controller.request.UpdateMinglerRequest
import semo.backend.controller.response.CreateMinglerResponse
import semo.backend.controller.response.DeleteMinglerResponse
import semo.backend.controller.response.GetMinglerResponse
import semo.backend.controller.response.GetMinglersResponse
import semo.backend.controller.response.UpdateMinglerResponse
import semo.backend.facade.MinglerFacade
import semo.backend.security.UserId

@RestController
@RequestMapping("/minglers")
class MinglerController(
    private val minglerFacade: MinglerFacade,
) {
    @GetMapping
    fun getMinglers(
        @UserId userId: Long,
    ): GetMinglersResponse {
        return GetMinglersResponse(
            minglers = minglerFacade.getMinglers(userId),
        )
    }

    @GetMapping("/{minglerId}")
    fun getMingler(
        @UserId userId: Long,
        @PathVariable minglerId: Long,
    ): GetMinglerResponse {
        return GetMinglerResponse(
            mingler = minglerFacade.getMingler(userId, minglerId),
        )
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createMingler(
        @UserId userId: Long,
        @RequestBody request: CreateMinglerRequest,
    ): CreateMinglerResponse {
        return CreateMinglerResponse(
            mingler = minglerFacade.createMingler(userId, request),
        )
    }

    @PutMapping("/{minglerId}")
    fun updateMingler(
        @UserId userId: Long,
        @PathVariable minglerId: Long,
        @OptionalRequestBody request: UpdateMinglerRequest,
    ): UpdateMinglerResponse {
        return UpdateMinglerResponse(
            mingler = minglerFacade.updateMingler(userId, minglerId, request),
        )
    }

    @DeleteMapping("/{minglerId}")
    fun deleteMingler(
        @UserId userId: Long,
        @PathVariable minglerId: Long,
    ): DeleteMinglerResponse {
        return DeleteMinglerResponse(
            deletedMinglerId = minglerFacade.deleteMingler(userId, minglerId),
        )
    }
}
