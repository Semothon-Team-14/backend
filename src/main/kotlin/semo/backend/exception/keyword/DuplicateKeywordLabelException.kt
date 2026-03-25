package semo.backend.exception.keyword

import org.springframework.http.HttpStatus
import semo.backend.exception.BaseCustomException

class DuplicateKeywordLabelException(
    label: String,
) : BaseCustomException(
    status = HttpStatus.CONFLICT,
    reasonTemplate = "Keyword label already exists: {label}",
    reasonVariables = mapOf("label" to label),
)
