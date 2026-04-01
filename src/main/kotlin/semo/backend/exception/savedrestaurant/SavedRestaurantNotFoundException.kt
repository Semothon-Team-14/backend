package semo.backend.exception.savedrestaurant

import org.springframework.http.HttpStatus
import semo.backend.exception.BaseCustomException

class SavedRestaurantNotFoundException(
    savedRestaurantId: Long,
) : BaseCustomException(
    status = HttpStatus.NOT_FOUND,
    reasonTemplate = "Saved restaurant not found for id={savedRestaurantId}",
    reasonVariables = mapOf("savedRestaurantId" to savedRestaurantId),
)
