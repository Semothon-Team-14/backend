package semo.backend.controller.request

data class CreateLocalRequest(
    val cityId: Long,
    val availableTimeText: String? = null,
)
