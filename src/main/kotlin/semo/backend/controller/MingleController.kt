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
import semo.backend.controller.request.CreateMingleRequest
import semo.backend.controller.request.UpdateMingleRequest
import semo.backend.controller.response.CreateMingleResponse
import semo.backend.controller.response.DeleteMingleResponse
import semo.backend.controller.response.GetMingleResponse
import semo.backend.controller.response.GetMinglesResponse
import semo.backend.controller.response.UpdateMingleResponse
import semo.backend.facade.MingleFacade

@RestController
@RequestMapping("/mingles")
class MingleController(
    private val mingleFacade: MingleFacade,
) {
    @GetMapping
    fun getMingles(): GetMinglesResponse {
        return GetMinglesResponse(
            mingles = mingleFacade.getMingles(),
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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createMingle(
        @RequestBody request: CreateMingleRequest,
    ): CreateMingleResponse {
        return CreateMingleResponse(
            mingle = mingleFacade.createMingle(request),
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
}
