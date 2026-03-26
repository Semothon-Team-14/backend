package semo.backend.controller.response

import semo.backend.dto.RestaurantDto

data class GetRestaurantsResponse(
    val restaurants: List<RestaurantDto>,
)
