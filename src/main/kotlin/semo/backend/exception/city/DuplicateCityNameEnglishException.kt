package semo.backend.exception.city

import org.springframework.http.HttpStatus
import semo.backend.exception.BaseCustomException

class DuplicateCityNameEnglishException(
    nationalityId: Long,
    cityNameEnglish: String,
) : BaseCustomException(
    status = HttpStatus.CONFLICT,
    reasonTemplate = "City English name already exists for nationality {nationalityId}: {cityNameEnglish}",
    reasonVariables = mapOf(
        "nationalityId" to nationalityId,
        "cityNameEnglish" to cityNameEnglish,
    ),
)
