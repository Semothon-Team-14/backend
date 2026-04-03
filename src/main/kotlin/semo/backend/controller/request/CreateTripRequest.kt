package semo.backend.controller.request

import java.time.LocalDate
import java.time.LocalDateTime

data class CreateTripRequest(
    val title: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val cityId: Long,
    val fromCityId: Long? = null,
    val departureDateTime: LocalDateTime? = null,
    val departureLandingDateTime: LocalDateTime? = null,
)
