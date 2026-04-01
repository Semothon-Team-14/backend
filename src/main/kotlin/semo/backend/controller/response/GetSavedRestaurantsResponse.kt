package semo.backend.controller.response

import semo.backend.dto.SavedRestaurantDto

data class GetSavedRestaurantsResponse(
    val savedRestaurants: List<SavedRestaurantDto>,
)
