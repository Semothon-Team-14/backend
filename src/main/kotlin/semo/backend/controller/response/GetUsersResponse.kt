package semo.backend.controller.response

import semo.backend.dto.UserDto

data class GetUsersResponse(
    val users: List<UserDto>,
)
