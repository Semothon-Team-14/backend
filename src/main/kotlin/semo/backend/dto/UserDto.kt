package semo.backend.dto

data class UserDto(
    val id: Long,
    val username: String?,
    val name: String?,
    val email: String?,
    val phone: String?,
    val introduction: String?,
    val nationalityId: Long?,
    val keywords: List<KeywordDto>,
)
