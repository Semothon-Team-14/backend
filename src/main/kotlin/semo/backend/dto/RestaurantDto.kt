package semo.backend.dto

import java.math.BigDecimal

data class RestaurantDto(
    val id: Long,
    val cityId: Long,
    val name: String,
    val phoneNumber: String?,
    val address: String?,
    val foodCategory: String?,
    val latitude: BigDecimal?,
    val longitude: BigDecimal?,
)
