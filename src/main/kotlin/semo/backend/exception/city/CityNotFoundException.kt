package semo.backend.exception.city

import org.springframework.http.HttpStatus
import semo.backend.exception.BaseCustomException

class CityNotFoundException(
    cityId: Long,
) : BaseCustomException(
    status = HttpStatus.NOT_FOUND,
    reasonTemplate = "City not found for id={cityId}",
    reasonVariables = mapOf("cityId" to cityId),
)
