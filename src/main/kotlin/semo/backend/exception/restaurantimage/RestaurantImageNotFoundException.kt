package semo.backend.exception.restaurantimage

import org.springframework.http.HttpStatus
import semo.backend.exception.BaseCustomException

class RestaurantImageNotFoundException(
    restaurantImageId: Long,
) : BaseCustomException(
    status = HttpStatus.NOT_FOUND,
    reasonTemplate = "Restaurant image not found for id={restaurantImageId}",
    reasonVariables = mapOf("restaurantImageId" to restaurantImageId),
)
