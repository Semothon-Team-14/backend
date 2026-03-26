package semo.backend.dto

data class CafeImageDto(
    val id: Long,
    val cafeId: Long,
    val imageUrl: String,
    val mainImage: Boolean,
)
