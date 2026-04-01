package semo.backend.dto

import java.time.LocalDateTime

data class SavedRestaurantDto(
    val id: Long,
    val userId: Long,
    val restaurant: RestaurantDto,
    val createdDateTime: LocalDateTime,
    val updatedDateTime: LocalDateTime,
)
