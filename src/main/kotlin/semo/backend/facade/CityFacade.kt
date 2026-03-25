package semo.backend.facade

import org.springframework.stereotype.Service
import semo.backend.controller.request.CreateCityRequest
import semo.backend.controller.request.UpdateCityRequest
import semo.backend.dto.CityDto
import semo.backend.service.CityService

@Service
class CityFacade(
    private val cityService: CityService,
) {
    fun getCities(nationalityId: Long): List<CityDto> {
        return cityService.getCities(nationalityId)
    }

    fun getCity(nationalityId: Long, cityId: Long): CityDto {
        return cityService.getCity(nationalityId, cityId)
    }

    fun createCity(nationalityId: Long, request: CreateCityRequest): CityDto {
        return cityService.createCity(nationalityId, request)
    }

    fun updateCity(nationalityId: Long, cityId: Long, request: UpdateCityRequest): CityDto {
        return cityService.updateCity(nationalityId, cityId, request)
    }

    fun deleteCity(nationalityId: Long, cityId: Long): Long {
        return cityService.deleteCity(nationalityId, cityId)
    }
}
