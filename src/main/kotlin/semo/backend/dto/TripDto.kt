package semo.backend.dto

import java.time.LocalDate

data class TripDto(
    val id: Long,
    val title: String?,
    val startDate: LocalDate?,
    val endDate: LocalDate?,
    val userId: Long?,
    val cityId: Long?,
)
