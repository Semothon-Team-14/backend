package semo.backend.dto

data class CityDto(
    val id: Long,
    val nationalityId: Long,
    val cityNameEnglish: String,
    val cityNameKorean: String,
    val representativeImageUrl: String?,
)
