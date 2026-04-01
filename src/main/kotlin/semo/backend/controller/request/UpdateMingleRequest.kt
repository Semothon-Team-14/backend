package semo.backend.controller.request

import java.util.Optional

data class UpdateMingleRequest(
    val cityId: Optional<Long>? = null,
    val title: Optional<String>? = null,
    val description: Optional<String>? = null,
)
