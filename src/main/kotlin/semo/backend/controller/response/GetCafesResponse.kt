package semo.backend.controller.response

import semo.backend.dto.CafeDto

data class GetCafesResponse(
    val cafes: List<CafeDto>,
)
