package semo.backend.service

import org.springframework.stereotype.Service
import semo.backend.dto.UserDto
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
}