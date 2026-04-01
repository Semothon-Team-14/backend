package semo.backend.controller.request

import java.util.Optional

data class UpdateMinglerRequest(
    val mingleId: Optional<Long>? = null,
)
