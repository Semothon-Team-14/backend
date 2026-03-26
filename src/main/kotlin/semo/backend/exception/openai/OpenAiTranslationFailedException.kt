package semo.backend.exception.openai

import org.springframework.http.HttpStatus
import semo.backend.exception.BaseCustomException

class OpenAiTranslationFailedException(
) : BaseCustomException(
    status = HttpStatus.BAD_GATEWAY,
    reasonTemplate = "Failed to translate chat message with OpenAI",
)
