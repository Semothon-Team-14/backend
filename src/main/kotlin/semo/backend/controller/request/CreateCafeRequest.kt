package semo.backend.controller.request

import java.math.BigDecimal

data class CreateCafeRequest(
    val name: String,
    val phoneNumber: String? = null,
    val address: String? = null,
    val foodCategory: String? = null,
    val latitude: BigDecimal? = null,
    val longitude: BigDecimal? = null,
)
