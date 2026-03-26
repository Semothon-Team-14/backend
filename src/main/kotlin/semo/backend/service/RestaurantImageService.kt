package semo.backend.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import semo.backend.controller.request.UpdateRestaurantImageRequest
import semo.backend.dto.RestaurantImageDto
import semo.backend.entity.Restaurant
import semo.backend.entity.RestaurantImage
import semo.backend.exception.restaurantimage.RestaurantImageNotFoundException
import semo.backend.mapstruct.RestaurantImageMapStruct
import semo.backend.repository.jpa.RestaurantImageRepository
import semo.backend.storage.S3StorageService
import semo.backend.util.applyIfProvided

@Service
class RestaurantImageService(
    private val restaurantService: RestaurantService,
    private val restaurantImageRepository: RestaurantImageRepository,
    private val restaurantImageMapStruct: RestaurantImageMapStruct,
    private val s3StorageService: S3StorageService,
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
        file: MultipartFile,
        mainImage: Boolean,
    ): RestaurantImageDto {
        val restaurant = restaurantService.findRestaurantById(restaurantId)
        val storageObject = s3StorageService.upload("restaurants/$restaurantId/images", file)
        val restaurantImage = RestaurantImage(
            imageUrl = storageObject.url,
            s3Key = storageObject.key,
            mainImage = mainImage,
            restaurant = restaurant,
        )
        val savedImage = restaurantImageRepository.save(restaurantImage)
        if (mainImage) {
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
        s3StorageService.delete(restaurantImage.s3Key)
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
