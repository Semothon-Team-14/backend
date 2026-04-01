package semo.backend.controller.response

import semo.backend.dto.MinglerDto

data class GetMinglersResponse(
    val minglers: List<MinglerDto>,
)
