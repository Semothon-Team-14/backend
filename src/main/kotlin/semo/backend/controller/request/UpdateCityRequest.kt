package semo.backend.controller.request

import java.util.Optional

data class UpdateCityRequest(
    val cityNameEnglish: Optional<String>? = null,
    val cityNameKorean: Optional<String>? = null,
    val representativeImageUrl: Optional<String>? = null,
)
