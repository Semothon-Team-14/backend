package semo.backend.dto

import semo.backend.enums.Sex

data class UserDto(
    val id: Long,
    val username: String?,
    val name: String?,
    val email: String?,
    val phone: String?,
    val sex: Sex?,
    val introduction: String?,
    val nationalityId: Long?,
    val keywords: List<KeywordDto>,
)
