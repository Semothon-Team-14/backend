package semo.backend.exception.quickmatch

import org.springframework.http.HttpStatus
import semo.backend.exception.BaseCustomException

class QuickMatchNotFoundException(
    quickMatchId: Long,
) : BaseCustomException(
    status = HttpStatus.NOT_FOUND,
    reasonTemplate = "Quick match not found for id={quickMatchId}",
    reasonVariables = mapOf("quickMatchId" to quickMatchId),
)
