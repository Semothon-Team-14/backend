package semo.backend.dto

import java.math.BigDecimal

data class CityDto(
    val id: Long,
    val nationalityId: Long,
    val cityNameEnglish: String,
    val cityNameKorean: String,
    val representativeImageUrl: String?,
    val centerLatitude: BigDecimal?,
    val centerLongitude: BigDecimal?,
)
