package semo.backend.exception.savedrestaurant

import org.springframework.http.HttpStatus
import semo.backend.exception.BaseCustomException

class SavedRestaurantTargetRequiredException : BaseCustomException(
    status = HttpStatus.BAD_REQUEST,
    reasonTemplate = "restaurantId is required for a saved restaurant",
)
