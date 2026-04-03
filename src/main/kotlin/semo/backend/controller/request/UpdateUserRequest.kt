package semo.backend.controller.request

import semo.backend.enums.Sex
import java.util.Optional

data class UpdateUserRequest(
    val username: Optional<String>? = null,
    val password: Optional<String>? = null,
    val name: Optional<String>? = null,
    val email: Optional<String>? = null,
    val phone: Optional<String>? = null,
    val sex: Optional<Sex>? = null,
    val introduction: Optional<String>? = null,
    val profileImageUrl: Optional<String>? = null,
    val nationalityId: Optional<Long>? = null,
    val keywordIds: Optional<List<Long>>? = null,
)
