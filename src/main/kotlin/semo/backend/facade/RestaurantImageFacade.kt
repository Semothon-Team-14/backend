package semo.backend.facade

import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
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
        file: MultipartFile,
        mainImage: Boolean,
    ): RestaurantImageDto {
        return restaurantImageService.createRestaurantImage(restaurantId, file, mainImage)
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
