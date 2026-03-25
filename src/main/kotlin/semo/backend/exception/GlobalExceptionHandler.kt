package semo.backend.exception

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import semo.backend.exception.response.ErrorResponse

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(BaseCustomException::class)
    fun handleBaseCustomException(
        exception: BaseCustomException,
    ): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(exception.status)
            .body(
                ErrorResponse(
                    status = exception.status.value(),
                    error = exception.status.name,
                    reason = exception.reason,
                ),
            )
    }
}
