package semo.backend.controller.response

import semo.backend.dto.SavedRestaurantDto

data class CreateSavedRestaurantResponse(
    val savedRestaurant: SavedRestaurantDto,
)
