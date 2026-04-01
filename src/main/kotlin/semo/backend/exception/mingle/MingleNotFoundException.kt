package semo.backend.exception.mingle

import org.springframework.http.HttpStatus
import semo.backend.exception.BaseCustomException

class MingleNotFoundException(
    mingleId: Long,
) : BaseCustomException(
    status = HttpStatus.NOT_FOUND,
    reasonTemplate = "Mingle not found for id={mingleId}",
    reasonVariables = mapOf("mingleId" to mingleId),
)
