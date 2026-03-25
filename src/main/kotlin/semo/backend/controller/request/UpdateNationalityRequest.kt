package semo.backend.controller.request

import java.util.Optional

data class UpdateNationalityRequest(
    val countryCode: Optional<String>? = null,
    val countryNameEnglish: Optional<String>? = null,
    val countryNameKorean: Optional<String>? = null,
)
