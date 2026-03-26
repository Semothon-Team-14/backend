package semo.backend.exception.cafe

import org.springframework.http.HttpStatus
import semo.backend.exception.BaseCustomException

class CafeNotFoundException(
    cafeId: Long,
) : BaseCustomException(
    status = HttpStatus.NOT_FOUND,
    reasonTemplate = "Cafe not found for id={cafeId}",
    reasonVariables = mapOf("cafeId" to cafeId),
)
