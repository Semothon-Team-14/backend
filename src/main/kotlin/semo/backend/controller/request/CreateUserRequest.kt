package semo.backend.controller.request

data class CreateUserRequest(
    val username: String,
    val password: String,
    val name: String,
    val email: String,
    val phone: String,
    val introduction: String? = null,
    val nationalityId: Long? = null,
    val keywordIds: List<Long> = emptyList(),
)
