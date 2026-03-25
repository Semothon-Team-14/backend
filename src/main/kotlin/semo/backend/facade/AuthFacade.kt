package semo.backend.facade

import org.springframework.stereotype.Service
import semo.backend.controller.request.LoginRequest
import semo.backend.service.AuthService

@Service
class AuthFacade(
    private val authService: AuthService,
) {
    fun login(request: LoginRequest): String {
        return authService.login(request)
    }
}
