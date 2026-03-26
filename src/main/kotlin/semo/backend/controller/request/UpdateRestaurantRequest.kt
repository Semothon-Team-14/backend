package semo.backend.controller.request

import java.math.BigDecimal
import java.util.Optional

data class UpdateRestaurantRequest(
    val name: Optional<String>? = null,
    val phoneNumber: Optional<String>? = null,
    val address: Optional<String>? = null,
    val foodCategory: Optional<String>? = null,
    val latitude: Optional<BigDecimal>? = null,
    val longitude: Optional<BigDecimal>? = null,
)
