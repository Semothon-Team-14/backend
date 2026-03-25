package semo.backend.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import semo.backend.config.argument.OptionalRequestBody
import semo.backend.controller.request.CreateCityRequest
import semo.backend.controller.request.UpdateCityRequest
import semo.backend.controller.response.CreateCityResponse
import semo.backend.controller.response.DeleteCityResponse
import semo.backend.controller.response.GetCitiesResponse
import semo.backend.controller.response.GetCityResponse
import semo.backend.controller.response.UpdateCityResponse
import semo.backend.facade.CityFacade

@RestController
@RequestMapping("/cities/nationalities/{nationalityId}")
class CityController(
    private val cityFacade: CityFacade,
) {
    @GetMapping
    fun getCities(
        @PathVariable nationalityId: Long,
    ): GetCitiesResponse {
        return GetCitiesResponse(
            cities = cityFacade.getCities(nationalityId),
        )
    }

    @GetMapping("/{cityId}")
    fun getCity(
        @PathVariable nationalityId: Long,
        @PathVariable cityId: Long,
    ): GetCityResponse {
        return GetCityResponse(
            city = cityFacade.getCity(nationalityId, cityId),
        )
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createCity(
        @PathVariable nationalityId: Long,
        @RequestBody request: CreateCityRequest,
    ): CreateCityResponse {
        return CreateCityResponse(
            city = cityFacade.createCity(nationalityId, request),
        )
    }

    @PutMapping("/{cityId}")
    fun updateCity(
        @PathVariable nationalityId: Long,
        @PathVariable cityId: Long,
        @OptionalRequestBody request: UpdateCityRequest,
    ): UpdateCityResponse {
        return UpdateCityResponse(
            city = cityFacade.updateCity(nationalityId, cityId, request),
        )
    }

    @DeleteMapping("/{cityId}")
    fun deleteCity(
        @PathVariable nationalityId: Long,
        @PathVariable cityId: Long,
    ): DeleteCityResponse {
        return DeleteCityResponse(
            deletedCityId = cityFacade.deleteCity(nationalityId, cityId),
        )
    }
}
