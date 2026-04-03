package semo.backend.controller.request

data class RecognizeBoardingPassRequest(
    val cityId: Long? = null,
    val imageBase64: String,
)
