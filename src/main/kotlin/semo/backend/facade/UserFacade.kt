package semo.backend.facade

import org.springframework.stereotype.Service
import semo.backend.dto.UserDto

@Service
class UserFacade(
    private val userService: UserService,
) {
    fun getUsers(): List<UserDto>{
        return userService.getUsers()
    }
}
