package semo.backend.controller.request

import java.util.Optional

data class UpdateKeywordRequest(
    val label: Optional<String>? = null,
    val priority: Optional<Int>? = null,
)
