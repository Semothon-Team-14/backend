package semo.backend.exception.quickmatch

import org.springframework.http.HttpStatus
import semo.backend.exception.BaseCustomException

class QuickMatchSelfResponseNotAllowedException(
    quickMatchId: Long,
    userId: Long,
) : BaseCustomException(
    status = HttpStatus.BAD_REQUEST,
    reasonTemplate = "User id={userId} cannot respond to own quick match id={quickMatchId}",
    reasonVariables = mapOf("userId" to userId, "quickMatchId" to quickMatchId),
)
