package semo.backend.controller.request

import java.util.Optional

data class UpdateSavedCafeRequest(
    val cafeId: Optional<Long>? = null,
)
