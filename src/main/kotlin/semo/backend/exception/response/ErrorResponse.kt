package semo.backend.exception.response

data class ErrorResponse(
    val status: Int,
    val error: String,
    val reason: String,
)
