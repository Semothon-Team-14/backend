package semo.backend.controller

import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import semo.backend.controller.request.LoginRequest
import semo.backend.controller.response.LoginResponse
import semo.backend.facade.AuthFacade

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authFacade: AuthFacade,
) {
    @Operation(security = [])
    @PostMapping("/login")
    fun login(
        @RequestBody request: LoginRequest,
    ): LoginResponse {
        return LoginResponse(
            accessToken = authFacade.login(request),
        )
    }
}
