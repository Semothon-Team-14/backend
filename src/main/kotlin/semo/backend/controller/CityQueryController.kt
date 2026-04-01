package semo.backend.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import semo.backend.controller.response.GetCitiesByNationalityResponse
import semo.backend.facade.CityFacade

@RestController
@RequestMapping("/cities")
class CityQueryController(
    private val cityFacade: CityFacade,
) {
    @GetMapping
    fun getCitiesByNationality(): GetCitiesByNationalityResponse {
        return GetCitiesByNationalityResponse(
            nationalities = cityFacade.getCitiesByNationality(),
        )
    }
}
