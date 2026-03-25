package semo.backend.service

import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import semo.backend.controller.request.CreateUserRequest
import semo.backend.controller.request.UpdateUserRequest
import semo.backend.dto.UserDto
import semo.backend.entity.User
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

    fun createUser(request: CreateUserRequest): UserDto {
        val savedUser = userRepository.save(userMapStruct.toEntity(request))
        return userMapStruct.toDto(savedUser)
    }

    fun updateUser(userId: Long, request: UpdateUserRequest): UserDto {
        val user = findUserById(userId)
        userMapStruct.updateEntityFromRequest(request, user)
        return userMapStruct.toDto(userRepository.save(user))
    }

    fun deleteUser(userId: Long): Long {
        val user = findUserById(userId)
        userRepository.delete(user)
        return userId
    }

    private fun findUserById(userId: Long): User {
        return userRepository.findById(userId)
            .orElseThrow { EntityNotFoundException("User not found for id=$userId") }
    }
}
