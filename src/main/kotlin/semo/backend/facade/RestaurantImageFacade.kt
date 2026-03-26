package semo.backend.facade

import org.springframework.stereotype.Service
import semo.backend.controller.request.CreateRestaurantImageRequest
import semo.backend.controller.request.UpdateRestaurantImageRequest
import semo.backend.dto.RestaurantImageDto
import semo.backend.service.RestaurantImageService

@Service
class RestaurantImageFacade(
    private val restaurantImageService: RestaurantImageService,
) {
    fun getRestaurantImages(restaurantId: Long): List<RestaurantImageDto> {
        return restaurantImageService.getRestaurantImages(restaurantId)
    }

    fun getRestaurantImage(restaurantId: Long, imageId: Long): RestaurantImageDto {
        return restaurantImageService.getRestaurantImage(restaurantId, imageId)
    }

    fun createRestaurantImage(
        restaurantId: Long,
        request: CreateRestaurantImageRequest,
    ): RestaurantImageDto {
        return restaurantImageService.createRestaurantImage(restaurantId, request)
    }

    fun updateRestaurantImage(
        restaurantId: Long,
        imageId: Long,
        request: UpdateRestaurantImageRequest,
    ): RestaurantImageDto {
        return restaurantImageService.updateRestaurantImage(restaurantId, imageId, request)
    }

    fun deleteRestaurantImage(restaurantId: Long, imageId: Long): Long {
        return restaurantImageService.deleteRestaurantImage(restaurantId, imageId)
    }
}
