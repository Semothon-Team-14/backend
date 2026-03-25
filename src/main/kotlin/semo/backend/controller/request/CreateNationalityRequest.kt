package semo.backend.controller.request

data class CreateNationalityRequest(
    val countryCode: String,
    val countryNameEnglish: String,
    val countryNameKorean: String,
)
