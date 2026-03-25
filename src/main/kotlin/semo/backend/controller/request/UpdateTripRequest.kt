package semo.backend.controller.request

import java.time.LocalDate
import java.util.Optional

data class UpdateTripRequest(
    val title: Optional<String>? = null,
    val startDate: Optional<LocalDate>? = null,
    val endDate: Optional<LocalDate>? = null,
    val userId: Optional<Long>? = null,
    val cityId: Optional<Long>? = null,
)
