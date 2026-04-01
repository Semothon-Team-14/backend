package semo.backend.controller.request

import java.util.Optional

data class UpdateSavedRestaurantRequest(
    val restaurantId: Optional<Long>? = null,
)
