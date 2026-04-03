package semo.backend.controller.request

import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.Optional

data class UpdateMingleRequest(
    val cityId: Optional<Long>? = null,
    val title: Optional<String>? = null,
    val description: Optional<String>? = null,
    val placeName: Optional<String>? = null,
    val meetDateTime: Optional<LocalDateTime>? = null,
    val latitude: Optional<BigDecimal>? = null,
    val longitude: Optional<BigDecimal>? = null,
)
