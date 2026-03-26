package semo.backend.exception.chat

import org.springframework.http.HttpStatus
import semo.backend.exception.BaseCustomException

class EmptyChatTranslationContentException(
) : BaseCustomException(
    status = HttpStatus.BAD_REQUEST,
    reasonTemplate = "Translated chat content must not be blank",
)
