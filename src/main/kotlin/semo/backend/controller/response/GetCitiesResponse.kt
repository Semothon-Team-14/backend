package semo.backend.controller.response

import semo.backend.dto.CityDto

data class GetCitiesResponse(
    val cities: List<CityDto>,
)
