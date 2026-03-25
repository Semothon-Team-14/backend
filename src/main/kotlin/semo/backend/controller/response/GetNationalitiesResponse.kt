package semo.backend.controller.response

import semo.backend.dto.NationalityDto

data class GetNationalitiesResponse(
    val nationalities: List<NationalityDto>,
)
