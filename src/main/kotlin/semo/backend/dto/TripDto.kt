package semo.backend.dto

import java.time.LocalDate
import java.time.LocalDateTime

data class TripDto(
    val id: Long,
    val title: String?,
    val startDate: LocalDate?,
    val endDate: LocalDate?,
    val departureDateTime: LocalDateTime?,
    val departureLandingDateTime: LocalDateTime?,
    val userId: Long?,
    val cityId: Long?,
    val fromCityId: Long?,
)
