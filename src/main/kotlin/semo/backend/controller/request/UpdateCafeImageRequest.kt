package semo.backend.controller.request

import java.util.Optional

data class UpdateCafeImageRequest(
    val mainImage: Optional<Boolean>? = null,
)
