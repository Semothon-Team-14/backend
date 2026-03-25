package semo.backend.controller.request

import java.time.LocalDate

data class UpdateTripRequest(
    val title: PatchValue<String?> = PatchValue.undefined(),
    val startDate: PatchValue<LocalDate?> = PatchValue.undefined(),
    val endDate: PatchValue<LocalDate?> = PatchValue.undefined(),
    val userId: PatchValue<Long?> = PatchValue.undefined(),
    val cityId: PatchValue<Long?> = PatchValue.undefined(),
)
