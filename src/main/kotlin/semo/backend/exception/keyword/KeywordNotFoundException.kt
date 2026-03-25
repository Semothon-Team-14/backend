package semo.backend.exception.keyword

import org.springframework.http.HttpStatus
import semo.backend.exception.BaseCustomException

class KeywordNotFoundException(
    keywordIds: Collection<Long>,
) : BaseCustomException(
    status = HttpStatus.NOT_FOUND,
    reasonTemplate = "Keyword not found for ids={keywordIds}",
    reasonVariables = mapOf("keywordIds" to keywordIds.sorted()),
)
