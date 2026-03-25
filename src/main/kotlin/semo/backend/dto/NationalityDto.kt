package semo.backend.dto

data class NationalityDto(
    val id: Long,
    val countryCode: String,
    val countryNameEnglish: String,
    val countryNameKorean: String,
)
