package semo.backend.exception.nationality

import org.springframework.http.HttpStatus
import semo.backend.exception.BaseCustomException

class InvalidNationalityCountryCodeException(
    countryCode: String,
) : BaseCustomException(
    status = HttpStatus.BAD_REQUEST,
    reasonTemplate = "Nationality country code must be exactly 2 uppercase letters: {countryCode}",
    reasonVariables = mapOf("countryCode" to countryCode),
)
