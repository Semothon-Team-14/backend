package semo.backend.dto

import java.time.LocalDateTime

data class MingleDto(
    val id: Long,
    val city: CityDto,
    val title: String,
    val description: String?,
    val createdDateTime: LocalDateTime,
    val updatedDateTime: LocalDateTime,
)
