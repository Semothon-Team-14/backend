package semo.backend.controller.request

data class CreateCafeImageRequest(
    val imageUrl: String,
    val mainImage: Boolean = false,
)
