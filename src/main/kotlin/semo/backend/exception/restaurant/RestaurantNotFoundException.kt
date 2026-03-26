package semo.backend.exception.restaurant

import org.springframework.http.HttpStatus
import semo.backend.exception.BaseCustomException

class RestaurantNotFoundException(
    restaurantId: Long,
) : BaseCustomException(
    status = HttpStatus.NOT_FOUND,
    reasonTemplate = "Restaurant not found for id={restaurantId}",
    reasonVariables = mapOf("restaurantId" to restaurantId),
)
