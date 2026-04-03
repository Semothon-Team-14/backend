package semo.backend.exception.ticket

import org.springframework.http.HttpStatus
import semo.backend.exception.BaseCustomException

class TicketRecognitionFailedException(
    detail: String? = null,
) : BaseCustomException(
    status = HttpStatus.BAD_REQUEST,
    reasonTemplate = "티켓 인식에 실패했습니다. {detail}",
    reasonVariables = mapOf("detail" to (detail ?: "지원되는 QR/PDF417 탑승권 이미지를 다시 시도해주세요.")),
)
