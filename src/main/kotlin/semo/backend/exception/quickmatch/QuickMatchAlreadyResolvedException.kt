package semo.backend.exception.quickmatch

import org.springframework.http.HttpStatus
import semo.backend.enums.QuickMatchStatus
import semo.backend.exception.BaseCustomException

class QuickMatchAlreadyResolvedException(
    quickMatchId: Long,
    status: QuickMatchStatus,
) : BaseCustomException(
    status = HttpStatus.CONFLICT,
    reasonTemplate = "Quick match id={quickMatchId} is already resolved with status={status}",
    reasonVariables = mapOf("quickMatchId" to quickMatchId, "status" to status.name),
)
