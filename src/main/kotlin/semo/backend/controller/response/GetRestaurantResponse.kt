package semo.backend.controller.response

import semo.backend.dto.RestaurantDto

data class GetRestaurantResponse(
    val restaurant: RestaurantDto,
)
