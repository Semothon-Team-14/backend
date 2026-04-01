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
import semo.backend.controller.request.CreateSavedCafeRequest
import semo.backend.controller.request.UpdateSavedCafeRequest
import semo.backend.controller.response.CreateSavedCafeResponse
import semo.backend.controller.response.DeleteSavedCafeResponse
import semo.backend.controller.response.GetSavedCafeResponse
import semo.backend.controller.response.GetSavedCafesResponse
import semo.backend.controller.response.UpdateSavedCafeResponse
import semo.backend.facade.SavedCafeFacade
import semo.backend.security.UserId

@RestController
@RequestMapping("/saved-cafes")
class SavedCafeController(
    private val savedCafeFacade: SavedCafeFacade,
) {
    @GetMapping
    fun getSavedCafes(
        @UserId userId: Long,
    ): GetSavedCafesResponse {
        return GetSavedCafesResponse(
            savedCafes = savedCafeFacade.getSavedCafes(userId),
        )
    }

    @GetMapping("/{savedCafeId}")
    fun getSavedCafe(
        @UserId userId: Long,
        @PathVariable savedCafeId: Long,
    ): GetSavedCafeResponse {
        return GetSavedCafeResponse(
            savedCafe = savedCafeFacade.getSavedCafe(userId, savedCafeId),
        )
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createSavedCafe(
        @UserId userId: Long,
        @RequestBody request: CreateSavedCafeRequest,
    ): CreateSavedCafeResponse {
        return CreateSavedCafeResponse(
            savedCafe = savedCafeFacade.createSavedCafe(userId, request),
        )
    }

    @PutMapping("/{savedCafeId}")
    fun updateSavedCafe(
        @UserId userId: Long,
        @PathVariable savedCafeId: Long,
        @OptionalRequestBody request: UpdateSavedCafeRequest,
    ): UpdateSavedCafeResponse {
        return UpdateSavedCafeResponse(
            savedCafe = savedCafeFacade.updateSavedCafe(userId, savedCafeId, request),
        )
    }

    @DeleteMapping("/{savedCafeId}")
    fun deleteSavedCafe(
        @UserId userId: Long,
        @PathVariable savedCafeId: Long,
    ): DeleteSavedCafeResponse {
        return DeleteSavedCafeResponse(
            deletedSavedCafeId = savedCafeFacade.deleteSavedCafe(userId, savedCafeId),
        )
    }
}
