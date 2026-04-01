package semo.backend.controller.request

import semo.backend.enums.QuickMatchTargetType

data class CreateQuickMatchRequest(
    val cityId: Long,
    val message: String? = null,
    val targetType: QuickMatchTargetType = QuickMatchTargetType.ANY,
)
