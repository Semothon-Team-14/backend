package semo.backend.dto

data class NationalityCitiesDto(
    val nationality: NationalityDto,
    val cities: List<CityDto>,
)
