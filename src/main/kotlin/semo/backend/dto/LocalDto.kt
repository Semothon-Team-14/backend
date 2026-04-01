package semo.backend.dto

import java.time.LocalDateTime

data class LocalDto(
    val id: Long,
    val userId: Long,
    val city: CityDto,
    val createdDateTime: LocalDateTime,
    val updatedDateTime: LocalDateTime,
)
