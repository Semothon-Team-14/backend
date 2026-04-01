package semo.backend.exception.savedcafe

import org.springframework.http.HttpStatus
import semo.backend.exception.BaseCustomException

class SavedCafeTargetRequiredException : BaseCustomException(
    status = HttpStatus.BAD_REQUEST,
    reasonTemplate = "cafeId is required for a saved cafe",
)
