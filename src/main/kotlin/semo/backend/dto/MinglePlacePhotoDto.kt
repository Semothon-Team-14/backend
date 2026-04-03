package semo.backend.dto

import java.time.LocalDateTime

data class MinglePlacePhotoDto(
    val id: Long,
    val mingleId: Long,
    val imageUrl: String,
    val createdDateTime: LocalDateTime,
    val updatedDateTime: LocalDateTime,
)
