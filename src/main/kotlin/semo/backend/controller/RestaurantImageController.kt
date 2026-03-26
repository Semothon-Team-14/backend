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
import semo.backend.controller.request.CreateRestaurantImageRequest
import semo.backend.controller.request.UpdateRestaurantImageRequest
import semo.backend.controller.response.CreateRestaurantImageResponse
import semo.backend.controller.response.DeleteRestaurantImageResponse
import semo.backend.controller.response.GetRestaurantImageResponse
import semo.backend.controller.response.GetRestaurantImagesResponse
import semo.backend.controller.response.UpdateRestaurantImageResponse
import semo.backend.facade.RestaurantImageFacade

@RestController
@RequestMapping("/restaurants/{restaurantId}/images")
class RestaurantImageController(
    private val restaurantImageFacade: RestaurantImageFacade,
) {
    @GetMapping
    fun getRestaurantImages(
        @PathVariable restaurantId: Long,
    ): GetRestaurantImagesResponse {
        return GetRestaurantImagesResponse(
            images = restaurantImageFacade.getRestaurantImages(restaurantId),
        )
    }

    @GetMapping("/{imageId}")
    fun getRestaurantImage(
        @PathVariable restaurantId: Long,
        @PathVariable imageId: Long,
    ): GetRestaurantImageResponse {
        return GetRestaurantImageResponse(
            image = restaurantImageFacade.getRestaurantImage(restaurantId, imageId),
        )
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createRestaurantImage(
        @PathVariable restaurantId: Long,
        @RequestBody request: CreateRestaurantImageRequest,
    ): CreateRestaurantImageResponse {
        return CreateRestaurantImageResponse(
            image = restaurantImageFacade.createRestaurantImage(restaurantId, request),
        )
    }

    @PutMapping("/{imageId}")
    fun updateRestaurantImage(
        @PathVariable restaurantId: Long,
        @PathVariable imageId: Long,
        @OptionalRequestBody request: UpdateRestaurantImageRequest,
    ): UpdateRestaurantImageResponse {
        return UpdateRestaurantImageResponse(
            image = restaurantImageFacade.updateRestaurantImage(restaurantId, imageId, request),
        )
    }

    @DeleteMapping("/{imageId}")
    fun deleteRestaurantImage(
        @PathVariable restaurantId: Long,
        @PathVariable imageId: Long,
    ): DeleteRestaurantImageResponse {
        return DeleteRestaurantImageResponse(
            deletedImageId = restaurantImageFacade.deleteRestaurantImage(restaurantId, imageId),
        )
    }
}
