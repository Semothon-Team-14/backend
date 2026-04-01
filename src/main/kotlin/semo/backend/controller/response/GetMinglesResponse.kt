package semo.backend.controller.response

import semo.backend.dto.MingleDto

data class GetMinglesResponse(
    val mingles: List<MingleDto>,
)
