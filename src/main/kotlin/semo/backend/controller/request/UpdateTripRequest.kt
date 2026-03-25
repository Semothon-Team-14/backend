package semo.backend.controller.request

import org.openapitools.jackson.nullable.JsonNullable
import java.time.LocalDate

data class UpdateTripRequest(
    val title: JsonNullable<String?> = JsonNullable.undefined(),
    val startDate: JsonNullable<LocalDate?> = JsonNullable.undefined(),
    val endDate: JsonNullable<LocalDate?> = JsonNullable.undefined(),
    val userId: JsonNullable<Long?> = JsonNullable.undefined(),
    val cityId: JsonNullable<Long?> = JsonNullable.undefined(),
)
