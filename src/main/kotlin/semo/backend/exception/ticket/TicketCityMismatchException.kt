package semo.backend.exception.ticket

import org.springframework.http.HttpStatus
import semo.backend.exception.BaseCustomException

class TicketCityMismatchException(
    expectedCityId: Long,
    actualCityId: Long,
) : BaseCustomException(
    status = HttpStatus.BAD_REQUEST,
    reasonTemplate = "선택한 도시와 티켓의 목적지가 일치하지 않습니다. expectedCityId={expectedCityId}, actualCityId={actualCityId}",
    reasonVariables = mapOf(
        "expectedCityId" to expectedCityId,
        "actualCityId" to actualCityId,
    ),
)
