package semo.backend.facade

import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import semo.backend.dto.MinglePlacePhotoDto
import semo.backend.service.MinglePlacePhotoService

@Service
class MinglePlacePhotoFacade(
    private val minglePlacePhotoService: MinglePlacePhotoService,
) {
    fun getMinglePlacePhotos(mingleId: Long): List<MinglePlacePhotoDto> {
        return minglePlacePhotoService.getMinglePlacePhotos(mingleId)
    }

    fun uploadMinglePlacePhoto(
        userId: Long,
        mingleId: Long,
        file: MultipartFile,
    ): MinglePlacePhotoDto {
        return minglePlacePhotoService.uploadMinglePlacePhoto(userId, mingleId, file)
    }

    fun deleteMinglePlacePhoto(
        userId: Long,
        mingleId: Long,
        photoId: Long,
    ): Long {
        return minglePlacePhotoService.deleteMinglePlacePhoto(userId, mingleId, photoId)
    }
}
