package semo.backend.service

import org.openapitools.jackson.nullable.JsonNullable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import semo.backend.controller.request.CreateUserRequest
import semo.backend.controller.request.UpdateUserRequest
import semo.backend.dto.UserDto
import semo.backend.entity.User
import semo.backend.exception.user.UserNotFoundException
import semo.backend.mapstruct.UserMapStruct
import semo.backend.repository.jpa.UserRepository

@Service
class UserService(
    private val userRepository: UserRepository,
    private val userMapStruct: UserMapStruct,
) {
    fun getUsers(): List<UserDto> {
        return userMapStruct.toDtos(userRepository.findAll())
    }

    fun getUser(userId: Long): UserDto {
        return userMapStruct.toDto(findUserById(userId))
    }

    @Transactional
    fun createUser(request: CreateUserRequest): UserDto {
        val savedUser = userRepository.save(userMapStruct.toEntity(request))
        return userMapStruct.toDto(savedUser)
    }

    @Transactional
    fun updateUser(userId: Long, request: UpdateUserRequest): UserDto {
        val user = findUserById(userId)
        request.username.ifPresent { user.username = it }
        request.password.ifPresent { user.password = it }
        request.name.ifPresent { user.name = it }
        request.email.ifPresent { user.email = it }
        request.phone.ifPresent { user.phone = it }
        return userMapStruct.toDto(userRepository.save(user))
    }

    @Transactional
    fun deleteUser(userId: Long): Long {
        val user = findUserById(userId)
        userRepository.delete(user)
        return userId
    }

    private fun findUserById(userId: Long): User {
        return userRepository.findById(userId)
            .orElseThrow { UserNotFoundException(userId) }
    }

    private inline fun <T> JsonNullable<T>.ifPresent(
        block: (T) -> Unit,
    ) {
        if (isPresent) {
            block(get())
        }
    }
}
