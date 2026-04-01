package semo.backend.exception.quickmatch

import org.springframework.http.HttpStatus
import semo.backend.exception.BaseCustomException

class QuickMatchResponderNotEligibleException(
    userId: Long,
    cityId: Long,
) : BaseCustomException(
    status = HttpStatus.FORBIDDEN,
    reasonTemplate = "User id={userId} is not eligible for quick match in city id={cityId}",
    reasonVariables = mapOf("userId" to userId, "cityId" to cityId),
)
