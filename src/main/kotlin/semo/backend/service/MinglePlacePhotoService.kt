package semo.backend.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import semo.backend.config.AwsS3Properties
import semo.backend.dto.MinglePlacePhotoDto
import semo.backend.entity.MinglePlacePhoto
import semo.backend.exception.mingle.MingleNotFoundException
import semo.backend.exception.mingleplacephoto.InvalidMinglePlacePhotoException
import semo.backend.exception.mingleplacephoto.MinglePlacePhotoNotFoundException
import semo.backend.exception.mingleplacephoto.MinglePlacePhotoStorageNotConfiguredException
import semo.backend.mapstruct.MinglePlacePhotoMapStruct
import semo.backend.repository.jpa.MinglePlacePhotoRepository
import semo.backend.repository.jpa.MingleRepository
import java.util.UUID

@Service
@Transactional(readOnly = true)
class MinglePlacePhotoService(
    private val mingleRepository: MingleRepository,
    private val minglePlacePhotoRepository: MinglePlacePhotoRepository,
    private val minglePlacePhotoMapStruct: MinglePlacePhotoMapStruct,
    private val minglerService: MinglerService,
    private val awsS3Properties: AwsS3Properties,
    private val awsS3StorageService: AwsS3StorageService? = null,
) {
    fun getMinglePlacePhotos(mingleId: Long): List<MinglePlacePhotoDto> {
        ensureMingleExists(mingleId)
        return minglePlacePhotoMapStruct.toDtos(minglePlacePhotoRepository.findAllByMingleIdOrderByCreatedDateTimeAsc(mingleId))
    }

    @Transactional
    fun uploadMinglePlacePhoto(
        userId: Long,
        mingleId: Long,
        file: MultipartFile,
    ): MinglePlacePhotoDto {
        minglerService.validateMingler(userId, mingleId)
        val storageService = awsS3StorageService ?: throw MinglePlacePhotoStorageNotConfiguredException()
        validateImageFile(file)

        val mingle = mingleRepository.findById(mingleId)
            .orElseThrow { MingleNotFoundException(mingleId) }
        val extension = resolveFileExtension(file.originalFilename, file.contentType.orEmpty())
        val key = "mingles/$mingleId/place-photos/${UUID.randomUUID()}.$extension"
        val uploadedUrl = file.inputStream.use { inputStream ->
            storageService.uploadPublicObjectToBucket(
                bucket = awsS3Properties.placesBucket,
                key = key,
                contentType = file.contentType.orEmpty(),
                contentLength = file.size,
                inputStream = inputStream,
            )
        }

        val savedPhoto = minglePlacePhotoRepository.save(
            MinglePlacePhoto(
                mingle = mingle,
                imageUrl = uploadedUrl,
            ),
        )
        return minglePlacePhotoMapStruct.toDto(savedPhoto)
    }

    @Transactional
    fun deleteMinglePlacePhoto(
        userId: Long,
        mingleId: Long,
        photoId: Long,
    ): Long {
        minglerService.validateMingler(userId, mingleId)
        val photo = minglePlacePhotoRepository.findByIdAndMingleId(photoId, mingleId)
            ?: throw MinglePlacePhotoNotFoundException(photoId)
        minglePlacePhotoRepository.delete(photo)
        return photoId
    }

    private fun ensureMingleExists(mingleId: Long) {
        if (!mingleRepository.existsById(mingleId)) {
            throw MingleNotFoundException(mingleId)
        }
    }

    private fun validateImageFile(file: MultipartFile) {
        if (file.isEmpty || file.size <= 0L) {
            throw InvalidMinglePlacePhotoException("Mingle place photo file is empty")
        }

        val contentType = file.contentType?.trim().orEmpty()
        if (!contentType.startsWith("image/")) {
            throw InvalidMinglePlacePhotoException("Mingle place photo must be an image file")
        }
    }

    private fun resolveFileExtension(originalFilename: String?, contentType: String): String {
        val fromFilename = originalFilename
            ?.substringAfterLast('.', "")
            ?.trim()
            ?.lowercase()
            ?.takeIf { it.isNotEmpty() }
        if (fromFilename != null) {
            return fromFilename
        }

        return when (contentType.lowercase()) {
            "image/jpeg", "image/jpg" -> "jpg"
            "image/png" -> "png"
            "image/webp" -> "webp"
            "image/heic" -> "heic"
            else -> "bin"
        }
    }
}
