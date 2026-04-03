package semo.backend.controller.request

import java.math.BigDecimal
import java.util.Optional

data class UpdateCityRequest(
    val cityNameEnglish: Optional<String>? = null,
    val cityNameKorean: Optional<String>? = null,
    val representativeImageUrl: Optional<String>? = null,
    val centerLatitude: Optional<BigDecimal>? = null,
    val centerLongitude: Optional<BigDecimal>? = null,
)
