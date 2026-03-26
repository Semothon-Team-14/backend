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
import semo.backend.controller.request.CreateCafeImageRequest
import semo.backend.controller.request.UpdateCafeImageRequest
import semo.backend.controller.response.CreateCafeImageResponse
import semo.backend.controller.response.DeleteCafeImageResponse
import semo.backend.controller.response.GetCafeImageResponse
import semo.backend.controller.response.GetCafeImagesResponse
import semo.backend.controller.response.UpdateCafeImageResponse
import semo.backend.facade.CafeImageFacade

@RestController
@RequestMapping("/cafes/{cafeId}/images")
class CafeImageController(
    private val cafeImageFacade: CafeImageFacade,
) {
    @GetMapping
    fun getCafeImages(
        @PathVariable cafeId: Long,
    ): GetCafeImagesResponse {
        return GetCafeImagesResponse(
            images = cafeImageFacade.getCafeImages(cafeId),
        )
    }

    @GetMapping("/{imageId}")
    fun getCafeImage(
        @PathVariable cafeId: Long,
        @PathVariable imageId: Long,
    ): GetCafeImageResponse {
        return GetCafeImageResponse(
            image = cafeImageFacade.getCafeImage(cafeId, imageId),
        )
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createCafeImage(
        @PathVariable cafeId: Long,
        @RequestBody request: CreateCafeImageRequest,
    ): CreateCafeImageResponse {
        return CreateCafeImageResponse(
            image = cafeImageFacade.createCafeImage(cafeId, request),
        )
    }

    @PutMapping("/{imageId}")
    fun updateCafeImage(
        @PathVariable cafeId: Long,
        @PathVariable imageId: Long,
        @OptionalRequestBody request: UpdateCafeImageRequest,
    ): UpdateCafeImageResponse {
        return UpdateCafeImageResponse(
            image = cafeImageFacade.updateCafeImage(cafeId, imageId, request),
        )
    }

    @DeleteMapping("/{imageId}")
    fun deleteCafeImage(
        @PathVariable cafeId: Long,
        @PathVariable imageId: Long,
    ): DeleteCafeImageResponse {
        return DeleteCafeImageResponse(
            deletedImageId = cafeImageFacade.deleteCafeImage(cafeId, imageId),
        )
    }
}
