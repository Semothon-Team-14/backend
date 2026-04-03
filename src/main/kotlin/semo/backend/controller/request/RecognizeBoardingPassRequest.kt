package semo.backend.controller.request

data class RecognizeBoardingPassRequest(
    val cityId: Long,
    val imageBase64: String,
)
