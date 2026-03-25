package semo.backend.exception.trip

import org.springframework.http.HttpStatus
import semo.backend.exception.BaseCustomException

class TripNotFoundException(
    tripId: Long,
) : BaseCustomException(
    status = HttpStatus.NOT_FOUND,
    reasonTemplate = "Trip not found for id={tripId}",
    reasonVariables = mapOf("tripId" to tripId),
)
