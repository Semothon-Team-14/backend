package semo.backend.controller.response

import semo.backend.dto.CafeImageDto

data class GetCafeImagesResponse(
    val images: List<CafeImageDto>,
)
