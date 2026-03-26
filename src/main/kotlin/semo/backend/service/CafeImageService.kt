package semo.backend.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import semo.backend.controller.request.UpdateCafeImageRequest
import semo.backend.dto.CafeImageDto
import semo.backend.entity.Cafe
import semo.backend.entity.CafeImage
import semo.backend.exception.cafeimage.CafeImageNotFoundException
import semo.backend.mapstruct.CafeImageMapStruct
import semo.backend.repository.jpa.CafeImageRepository
import semo.backend.storage.S3StorageService
import semo.backend.util.applyIfProvided

@Service
class CafeImageService(
    private val cafeService: CafeService,
    private val cafeImageRepository: CafeImageRepository,
    private val cafeImageMapStruct: CafeImageMapStruct,
    private val s3StorageService: S3StorageService,
) {
    fun getCafeImages(cafeId: Long): List<CafeImageDto> {
        cafeService.findCafeById(cafeId)
        return cafeImageMapStruct.toDtos(cafeImageRepository.findAllByCafeIdOrderByIdAsc(cafeId))
    }

    fun getCafeImage(cafeId: Long, imageId: Long): CafeImageDto {
        return cafeImageMapStruct.toDto(findCafeImage(cafeId, imageId))
    }

    @Transactional
    fun createCafeImage(
        cafeId: Long,
        file: MultipartFile,
        mainImage: Boolean,
    ): CafeImageDto {
        val cafe = cafeService.findCafeById(cafeId)
        val storageObject = s3StorageService.upload("cafes/$cafeId/images", file)
        val cafeImage = CafeImage(
            imageUrl = storageObject.url,
            s3Key = storageObject.key,
            mainImage = mainImage,
            cafe = cafe,
        )
        val savedImage = cafeImageRepository.save(cafeImage)
        if (mainImage) {
            clearOtherMainImages(cafe, savedImage.id)
        }
        return cafeImageMapStruct.toDto(savedImage)
    }

    @Transactional
    fun updateCafeImage(
        cafeId: Long,
        imageId: Long,
        request: UpdateCafeImageRequest,
    ): CafeImageDto {
        val cafeImage = findCafeImage(cafeId, imageId)
        request.mainImage.applyIfProvided { mainImage ->
            cafeImage.mainImage = mainImage ?: cafeImage.mainImage
            if (cafeImage.mainImage) {
                clearOtherMainImages(cafeImage.cafe, cafeImage.id)
            }
        }
        return cafeImageMapStruct.toDto(cafeImageRepository.save(cafeImage))
    }

    @Transactional
    fun deleteCafeImage(cafeId: Long, imageId: Long): Long {
        val cafeImage = findCafeImage(cafeId, imageId)
        s3StorageService.delete(cafeImage.s3Key)
        cafeImageRepository.delete(cafeImage)
        return imageId
    }

    private fun clearOtherMainImages(
        cafe: Cafe,
        currentImageId: Long,
    ) {
        val imagesToUpdate = cafeImageRepository.findAllByCafeIdAndIdNot(cafe.id, currentImageId)
            .filter { it.mainImage }
        imagesToUpdate.forEach { it.mainImage = false }
        if (imagesToUpdate.isNotEmpty()) {
            cafeImageRepository.saveAll(imagesToUpdate)
        }
    }

    private fun findCafeImage(
        cafeId: Long,
        imageId: Long,
    ): CafeImage {
        cafeService.findCafeById(cafeId)
        return cafeImageRepository.findByIdAndCafeId(imageId, cafeId)
            ?: throw CafeImageNotFoundException(imageId)
    }
}
