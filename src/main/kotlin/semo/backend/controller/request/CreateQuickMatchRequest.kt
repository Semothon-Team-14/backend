package semo.backend.controller.request

data class CreateQuickMatchRequest(
    val cityId: Long,
    val message: String? = null,
)
