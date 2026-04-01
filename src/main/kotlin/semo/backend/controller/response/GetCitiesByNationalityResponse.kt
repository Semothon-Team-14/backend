package semo.backend.controller.response

import semo.backend.dto.NationalityCitiesDto

data class GetCitiesByNationalityResponse(
    val nationalities: List<NationalityCitiesDto>,
)
