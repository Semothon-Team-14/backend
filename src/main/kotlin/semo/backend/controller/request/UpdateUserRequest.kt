package semo.backend.controller.request

import org.openapitools.jackson.nullable.JsonNullable

data class UpdateUserRequest(
    val username: JsonNullable<String?> = JsonNullable.undefined(),
    val password: JsonNullable<String?> = JsonNullable.undefined(),
    val name: JsonNullable<String?> = JsonNullable.undefined(),
    val email: JsonNullable<String?> = JsonNullable.undefined(),
    val phone: JsonNullable<String?> = JsonNullable.undefined(),
    val introduction: JsonNullable<String?> = JsonNullable.undefined(),
    val nationalityId: JsonNullable<Long?> = JsonNullable.undefined(),
    val keywordIds: JsonNullable<List<Long>?> = JsonNullable.undefined(),
)
