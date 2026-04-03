package semo.backend.dto

import java.time.LocalDateTime
import java.math.BigDecimal

data class MingleDto(
    val id: Long,
    val city: CityDto,
    val title: String,
    val description: String?,
    val latitude: BigDecimal?,
    val longitude: BigDecimal?,
    val createdDateTime: LocalDateTime,
    val updatedDateTime: LocalDateTime,
)
