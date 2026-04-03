package semo.backend.controller.request

import java.math.BigDecimal
import java.time.LocalDateTime

data class CreateMingleRequest(
    val cityId: Long,
    val title: String,
    val description: String? = null,
    val placeName: String? = null,
    val meetDateTime: LocalDateTime? = null,
    val latitude: BigDecimal? = null,
    val longitude: BigDecimal? = null,
)
