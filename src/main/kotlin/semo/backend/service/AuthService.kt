package semo.backend.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import semo.backend.controller.request.LoginRequest
import semo.backend.exception.auth.InvalidLoginException
import semo.backend.repository.jpa.UserRepository
import semo.backend.security.AccessTokenService

@Service
@Transactional(readOnly = true)
class AuthService(
    private val userRepository: UserRepository,
    private val accessTokenService: AccessTokenService,
) {
    fun login(request: LoginRequest): String {
        val user = userRepository.findByUsername(request.username)
            ?: throw InvalidLoginException()

        if (user.password != request.password) {
            throw InvalidLoginException()
        }

        return accessTokenService.createToken(user.id)
    }
}
