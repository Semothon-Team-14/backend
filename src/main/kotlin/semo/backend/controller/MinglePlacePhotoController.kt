package semo.backend.controller

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import semo.backend.controller.response.CreateMinglePlacePhotoResponse
import semo.backend.controller.response.DeleteMinglePlacePhotoResponse
import semo.backend.controller.response.GetMinglePlacePhotosResponse
import semo.backend.facade.MinglePlacePhotoFacade
import semo.backend.security.UserId

@RestController
@RequestMapping("/mingles/{mingleId}/place-photos")
class MinglePlacePhotoController(
    private val minglePlacePhotoFacade: MinglePlacePhotoFacade,
) {
    @GetMapping
    fun getMinglePlacePhotos(
        @PathVariable mingleId: Long,
    ): GetMinglePlacePhotosResponse {
        return GetMinglePlacePhotosResponse(
            photos = minglePlacePhotoFacade.getMinglePlacePhotos(mingleId),
        )
    }

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @ResponseStatus(HttpStatus.CREATED)
    fun uploadMinglePlacePhoto(
        @UserId userId: Long,
        @PathVariable mingleId: Long,
        @RequestPart("file") file: MultipartFile,
    ): CreateMinglePlacePhotoResponse {
        return CreateMinglePlacePhotoResponse(
            photo = minglePlacePhotoFacade.uploadMinglePlacePhoto(userId, mingleId, file),
        )
    }

    @DeleteMapping("/{photoId}")
    fun deleteMinglePlacePhoto(
        @UserId userId: Long,
        @PathVariable mingleId: Long,
        @PathVariable photoId: Long,
    ): DeleteMinglePlacePhotoResponse {
        return DeleteMinglePlacePhotoResponse(
            deletedPhotoId = minglePlacePhotoFacade.deleteMinglePlacePhoto(userId, mingleId, photoId),
        )
    }
}
