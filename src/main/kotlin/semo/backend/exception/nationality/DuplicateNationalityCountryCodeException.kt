package semo.backend.exception.nationality

import org.springframework.http.HttpStatus
import semo.backend.exception.BaseCustomException

class DuplicateNationalityCountryCodeException(
    countryCode: String,
) : BaseCustomException(
    status = HttpStatus.CONFLICT,
    reasonTemplate = "Nationality country code already exists: {countryCode}",
    reasonVariables = mapOf("countryCode" to countryCode),
)
