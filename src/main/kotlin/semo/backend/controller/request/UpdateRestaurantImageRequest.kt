package semo.backend.controller.request

import java.util.Optional

data class UpdateRestaurantImageRequest(
    val imageUrl: Optional<String>? = null,
    val mainImage: Optional<Boolean>? = null,
)
