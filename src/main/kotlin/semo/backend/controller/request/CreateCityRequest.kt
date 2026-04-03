package semo.backend.controller.request

import java.math.BigDecimal

data class CreateCityRequest(
    val cityNameEnglish: String,
    val cityNameKorean: String,
    val representativeImageUrl: String? = null,
    val centerLatitude: BigDecimal? = null,
    val centerLongitude: BigDecimal? = null,
)
