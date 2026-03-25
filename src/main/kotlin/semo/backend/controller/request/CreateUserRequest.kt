package semo.backend.controller.request

import semo.backend.enums.Sex

data class CreateUserRequest(
    val username: String,
    val password: String,
    val name: String,
    val email: String,
    val phone: String,
    val sex: Sex? = null,
    val introduction: String? = null,
    val nationalityId: Long? = null,
    val keywordIds: List<Long> = emptyList(),
)
