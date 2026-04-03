package semo.backend.controller.request

import java.math.BigDecimal

data class CreateMingleRequest(
    val cityId: Long,
    val title: String,
    val description: String? = null,
    val latitude: BigDecimal? = null,
    val longitude: BigDecimal? = null,
)
