package semo.backend.exception.savedrestaurant

import org.springframework.http.HttpStatus
import semo.backend.exception.BaseCustomException

class SavedRestaurantDuplicateException(
    userId: Long,
    restaurantId: Long,
) : BaseCustomException(
    status = HttpStatus.CONFLICT,
    reasonTemplate = "Saved restaurant already exists for userId={userId} and restaurantId={restaurantId}",
    reasonVariables = mapOf("userId" to userId, "restaurantId" to restaurantId),
)
