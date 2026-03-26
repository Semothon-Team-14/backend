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
import semo.backend.controller.request.CreateCafeRequest
import semo.backend.controller.request.UpdateCafeRequest
import semo.backend.controller.response.CreateCafeResponse
import semo.backend.controller.response.DeleteCafeResponse
import semo.backend.controller.response.GetCafeResponse
import semo.backend.controller.response.GetCafesResponse
import semo.backend.controller.response.UpdateCafeResponse
import semo.backend.facade.CafeFacade

@RestController
@RequestMapping("/cafes/cities/{cityId}")
class CafeController(
    private val cafeFacade: CafeFacade,
) {
    @GetMapping
    fun getCafes(
        @PathVariable cityId: Long,
    ): GetCafesResponse {
        return GetCafesResponse(
            cafes = cafeFacade.getCafes(cityId),
        )
    }

    @GetMapping("/{cafeId}")
    fun getCafe(
        @PathVariable cityId: Long,
        @PathVariable cafeId: Long,
    ): GetCafeResponse {
        return GetCafeResponse(
            cafe = cafeFacade.getCafe(cityId, cafeId),
        )
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createCafe(
        @PathVariable cityId: Long,
        @RequestBody request: CreateCafeRequest,
    ): CreateCafeResponse {
        return CreateCafeResponse(
            cafe = cafeFacade.createCafe(cityId, request),
        )
    }

    @PutMapping("/{cafeId}")
    fun updateCafe(
        @PathVariable cityId: Long,
        @PathVariable cafeId: Long,
        @OptionalRequestBody request: UpdateCafeRequest,
    ): UpdateCafeResponse {
        return UpdateCafeResponse(
            cafe = cafeFacade.updateCafe(cityId, cafeId, request),
        )
    }

    @DeleteMapping("/{cafeId}")
    fun deleteCafe(
        @PathVariable cityId: Long,
        @PathVariable cafeId: Long,
    ): DeleteCafeResponse {
        return DeleteCafeResponse(
            deletedCafeId = cafeFacade.deleteCafe(cityId, cafeId),
        )
    }
}
