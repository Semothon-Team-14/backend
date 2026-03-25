package semo.backend.exception.nationality

import org.springframework.http.HttpStatus
import semo.backend.exception.BaseCustomException

class NationalityNotFoundException(
    nationalityId: Long,
) : BaseCustomException(
    status = HttpStatus.NOT_FOUND,
    reasonTemplate = "Nationality not found for id={nationalityId}",
    reasonVariables = mapOf("nationalityId" to nationalityId),
)
