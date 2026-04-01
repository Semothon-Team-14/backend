package semo.backend.exception.savedcafe

import org.springframework.http.HttpStatus
import semo.backend.exception.BaseCustomException

class SavedCafeNotFoundException(
    savedCafeId: Long,
) : BaseCustomException(
    status = HttpStatus.NOT_FOUND,
    reasonTemplate = "Saved cafe not found for id={savedCafeId}",
    reasonVariables = mapOf("savedCafeId" to savedCafeId),
)
