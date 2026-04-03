package semo.backend.exception.ticket

import org.springframework.http.HttpStatus
import semo.backend.exception.BaseCustomException

class TicketAirportUnsupportedException(
    airportCode: String,
) : BaseCustomException(
    status = HttpStatus.BAD_REQUEST,
    reasonTemplate = "지원하지 않는 도착 공항 코드입니다. {airportCode}",
    reasonVariables = mapOf("airportCode" to airportCode),
)
