package semo.backend.controller.request

data class UpdateUserRequest(
    val username: String,
    val password: String,
    val name: String,
    val email: String,
    val phone: String,
)
