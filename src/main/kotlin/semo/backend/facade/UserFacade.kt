package semo.backend.facade

import org.springframework.stereotype.Service
import semo.backend.controller.request.CreateUserRequest
import semo.backend.controller.request.UpdateUserRequest
import semo.backend.dto.UserDto
import semo.backend.service.UserService

@Service
class UserFacade(
    private val userService: UserService,
) {
    fun getUsers(): List<UserDto> {
        return userService.getUsers()
    }

    fun getUser(userId: Long): UserDto {
        return userService.getUser(userId)
    }

    fun createUser(request: CreateUserRequest): UserDto {
        return userService.createUser(request)
    }

    fun updateUser(userId: Long, request: UpdateUserRequest): UserDto {
        return userService.updateUser(userId, request)
    }

    fun deleteUser(userId: Long): Long {
        return userService.deleteUser(userId)
    }
}
