package semo.backend.controller.request

data class CreateMingleRequest(
    val cityId: Long,
    val title: String,
    val description: String? = null,
)
