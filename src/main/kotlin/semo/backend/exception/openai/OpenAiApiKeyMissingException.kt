package semo.backend.exception.openai

import org.springframework.http.HttpStatus
import semo.backend.exception.BaseCustomException

class OpenAiApiKeyMissingException(
) : BaseCustomException(
    status = HttpStatus.INTERNAL_SERVER_ERROR,
    reasonTemplate = "OpenAI API key is not configured",
)
