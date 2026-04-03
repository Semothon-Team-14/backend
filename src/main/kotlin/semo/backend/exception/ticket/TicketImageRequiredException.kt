package semo.backend.exception.ticket

import org.springframework.http.HttpStatus
import semo.backend.exception.BaseCustomException

class TicketImageRequiredException : BaseCustomException(
    status = HttpStatus.BAD_REQUEST,
    reasonTemplate = "티켓 이미지가 필요합니다.",
)
