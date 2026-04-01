package semo.backend.controller.response

import semo.backend.dto.SavedRestaurantDto

data class GetSavedRestaurantResponse(
    val savedRestaurant: SavedRestaurantDto,
)
