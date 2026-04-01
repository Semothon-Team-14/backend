package semo.backend.controller.response

import semo.backend.dto.LocalDto

data class GetLocalsResponse(
    val locals: List<LocalDto>,
)
