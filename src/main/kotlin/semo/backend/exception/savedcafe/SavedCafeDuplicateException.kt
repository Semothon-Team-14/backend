package semo.backend.exception.savedcafe

import org.springframework.http.HttpStatus
import semo.backend.exception.BaseCustomException

class SavedCafeDuplicateException(
    userId: Long,
    cafeId: Long,
) : BaseCustomException(
    status = HttpStatus.CONFLICT,
    reasonTemplate = "Saved cafe already exists for userId={userId} and cafeId={cafeId}",
    reasonVariables = mapOf("userId" to userId, "cafeId" to cafeId),
)
