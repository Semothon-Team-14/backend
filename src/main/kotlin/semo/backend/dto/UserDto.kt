package semo.backend.dto

data class UserDto(
    val id: Long,
    val username: String?,
    val name: String?,
    val email: String?,
    val phone: String?,
)
