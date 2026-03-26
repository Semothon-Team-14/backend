package semo.backend.controller.request

import java.util.Optional

data class UpdateRestaurantImageRequest(
    val mainImage: Optional<Boolean>? = null,
)
