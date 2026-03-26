package semo.backend.controller.response

import semo.backend.dto.RestaurantImageDto

data class GetRestaurantImagesResponse(
    val images: List<RestaurantImageDto>,
)
