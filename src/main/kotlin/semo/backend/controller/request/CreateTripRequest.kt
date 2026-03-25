package semo.backend.controller.request

import java.time.LocalDate

data class CreateTripRequest(
    val title: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val cityId: Long,
)
