package semo.backend.facade

import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import semo.backend.controller.request.UpdateCafeImageRequest
import semo.backend.dto.CafeImageDto
import semo.backend.service.CafeImageService

@Service
class CafeImageFacade(
    private val cafeImageService: CafeImageService,
) {
    fun getCafeImages(cafeId: Long): List<CafeImageDto> {
        return cafeImageService.getCafeImages(cafeId)
    }

    fun getCafeImage(cafeId: Long, imageId: Long): CafeImageDto {
        return cafeImageService.getCafeImage(cafeId, imageId)
    }

    fun createCafeImage(
        cafeId: Long,
        file: MultipartFile,
        mainImage: Boolean,
    ): CafeImageDto {
        return cafeImageService.createCafeImage(cafeId, file, mainImage)
    }

    fun updateCafeImage(
        cafeId: Long,
        imageId: Long,
        request: UpdateCafeImageRequest,
    ): CafeImageDto {
        return cafeImageService.updateCafeImage(cafeId, imageId, request)
    }

    fun deleteCafeImage(cafeId: Long, imageId: Long): Long {
        return cafeImageService.deleteCafeImage(cafeId, imageId)
    }
}
