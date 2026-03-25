package semo.backend.controller

import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import semo.backend.config.argument.OptionalRequestBody
import semo.backend.controller.request.CreateUserRequest
import semo.backend.controller.request.UpdateUserRequest
import semo.backend.controller.response.CreateUserResponse
import semo.backend.controller.response.DeleteUserResponse
import semo.backend.controller.response.GetUserResponse
import semo.backend.controller.response.GetUsersResponse
import semo.backend.controller.response.UpdateUserResponse
import semo.backend.facade.UserFacade

@RestController
@RequestMapping("/users")
class UserController(
    private val userFacade: UserFacade,
) {
    @GetMapping
    fun getUsers(): GetUsersResponse {
        return GetUsersResponse(
            users = userFacade.getUsers(),
        )
    }

    @GetMapping("/{userId}")
    fun getUser(
        @PathVariable userId: Long,
    ): GetUserResponse {
        return GetUserResponse(
            user = userFacade.getUser(userId),
        )
    }

    @Operation(security = [])
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createUser(
        @RequestBody request: CreateUserRequest,
    ): CreateUserResponse {
        return CreateUserResponse(
            user = userFacade.createUser(request),
        )
    }

    @PutMapping("/{userId}")
    fun updateUser(
        @PathVariable userId: Long,
        @OptionalRequestBody request: UpdateUserRequest,
    ): UpdateUserResponse {
        return UpdateUserResponse(
            user = userFacade.updateUser(userId, request),
        )
    }

    @DeleteMapping("/{userId}")
    fun deleteUser(
        @PathVariable userId: Long,
    ): DeleteUserResponse {
        return DeleteUserResponse(
            deletedUserId = userFacade.deleteUser(userId),
        )
    }
}
