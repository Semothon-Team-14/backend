package semo.backend.controller.request

import java.util.Optional

data class UpdateLocalRequest(
    val cityId: Optional<Long>? = null,
)
