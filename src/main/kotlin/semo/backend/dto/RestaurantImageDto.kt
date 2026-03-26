package semo.backend.dto

data class RestaurantImageDto(
    val id: Long,
    val restaurantId: Long,
    val imageUrl: String,
    val mainImage: Boolean,
)
