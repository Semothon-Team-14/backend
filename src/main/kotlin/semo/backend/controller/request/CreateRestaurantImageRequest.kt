package semo.backend.controller.request

data class CreateRestaurantImageRequest(
    val imageUrl: String,
    val mainImage: Boolean = false,
)
