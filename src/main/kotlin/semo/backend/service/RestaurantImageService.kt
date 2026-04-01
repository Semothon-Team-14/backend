package semo.backend.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import semo.backend.controller.request.CreateRestaurantImageRequest
import semo.backend.controller.request.UpdateRestaurantImageRequest
import semo.backend.dto.RestaurantImageDto
import semo.backend.entity.Restaurant
import semo.backend.entity.RestaurantImage
import semo.backend.exception.restaurantimage.RestaurantImageNotFoundException
import semo.backend.mapstruct.RestaurantImageMapStruct
import semo.backend.repository.jpa.RestaurantImageRepository
import semo.backend.util.applyIfProvided

@Service
@Transactional(readOnly = true)
class RestaurantImageService(
    private val restaurantService: RestaurantService,
    private val restaurantImageRepository: RestaurantImageRepository,
    private val restaurantImageMapStruct: RestaurantImageMapStruct,
) {
    fun getRestaurantImages(restaurantId: Long): List<RestaurantImageDto> {
        restaurantService.findRestaurantById(restaurantId)
        return restaurantImageMapStruct.toDtos(restaurantImageRepository.findAllByRestaurantIdOrderByIdAsc(restaurantId))
    }

    fun getRestaurantImage(restaurantId: Long, imageId: Long): RestaurantImageDto {
        return restaurantImageMapStruct.toDto(findRestaurantImage(restaurantId, imageId))
    }

    @Transactional
    fun createRestaurantImage(
        restaurantId: Long,
        request: CreateRestaurantImageRequest,
    ): RestaurantImageDto {
        val restaurant = restaurantService.findRestaurantById(restaurantId)
        val restaurantImage = RestaurantImage(
            imageUrl = request.imageUrl.trim(),
            mainImage = request.mainImage,
            restaurant = restaurant,
        )
        val savedImage = restaurantImageRepository.save(restaurantImage)
        if (request.mainImage) {
            clearOtherMainImages(restaurant, savedImage.id)
        }
        return restaurantImageMapStruct.toDto(savedImage)
    }

    @Transactional
    fun updateRestaurantImage(
        restaurantId: Long,
        imageId: Long,
        request: UpdateRestaurantImageRequest,
    ): RestaurantImageDto {
        val restaurantImage = findRestaurantImage(restaurantId, imageId)
        request.imageUrl.applyIfProvided { imageUrl ->
            restaurantImage.imageUrl = imageUrl?.trim() ?: restaurantImage.imageUrl
        }
        request.mainImage.applyIfProvided { mainImage ->
            restaurantImage.mainImage = mainImage ?: restaurantImage.mainImage
            if (restaurantImage.mainImage) {
                clearOtherMainImages(restaurantImage.restaurant, restaurantImage.id)
            }
        }
        return restaurantImageMapStruct.toDto(restaurantImageRepository.save(restaurantImage))
    }

    @Transactional
    fun deleteRestaurantImage(restaurantId: Long, imageId: Long): Long {
        val restaurantImage = findRestaurantImage(restaurantId, imageId)
        restaurantImageRepository.delete(restaurantImage)
        return imageId
    }

    private fun clearOtherMainImages(
        restaurant: Restaurant,
        currentImageId: Long,
    ) {
        val imagesToUpdate = restaurantImageRepository.findAllByRestaurantIdAndIdNot(restaurant.id, currentImageId)
            .filter { it.mainImage }
        imagesToUpdate.forEach { it.mainImage = false }
        if (imagesToUpdate.isNotEmpty()) {
            restaurantImageRepository.saveAll(imagesToUpdate)
        }
    }

    private fun findRestaurantImage(
        restaurantId: Long,
        imageId: Long,
    ): RestaurantImage {
        restaurantService.findRestaurantById(restaurantId)
        return restaurantImageRepository.findByIdAndRestaurantId(imageId, restaurantId)
            ?: throw RestaurantImageNotFoundException(imageId)
    }
}
