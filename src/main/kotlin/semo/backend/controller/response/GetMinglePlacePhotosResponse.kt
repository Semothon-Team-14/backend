package semo.backend.controller.response

import semo.backend.dto.MinglePlacePhotoDto

data class GetMinglePlacePhotosResponse(
    val photos: List<MinglePlacePhotoDto>,
)
