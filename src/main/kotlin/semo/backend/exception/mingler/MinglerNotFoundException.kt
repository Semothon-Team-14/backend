package semo.backend.exception.mingler

import org.springframework.http.HttpStatus
import semo.backend.exception.BaseCustomException

class MinglerNotFoundException(
    minglerId: Long,
) : BaseCustomException(
    status = HttpStatus.NOT_FOUND,
    reasonTemplate = "Mingler not found for id={minglerId}",
    reasonVariables = mapOf("minglerId" to minglerId),
)
