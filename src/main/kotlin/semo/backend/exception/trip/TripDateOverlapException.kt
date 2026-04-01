package semo.backend.exception.trip

import org.springframework.http.HttpStatus
import semo.backend.exception.BaseCustomException
import java.time.LocalDate

class TripDateOverlapException(
    userId: Long,
    startDate: LocalDate,
    endDate: LocalDate,
) : BaseCustomException(
    status = HttpStatus.CONFLICT,
    reasonTemplate = "Trip dates overlap for userId={userId}, range={startDate}~{endDate}",
    reasonVariables = mapOf(
        "userId" to userId,
        "startDate" to startDate,
        "endDate" to endDate,
    ),
)
