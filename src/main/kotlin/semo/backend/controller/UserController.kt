package semo.backend.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import semo.backend.controller.response.GetUsersResponse

@RestController
@RequestMapping("/users")
class UserController {
    @GetMapping
    fun getUsers(): GetUsersResponse{
        return GetUsersResponse(
            users = userFacade.getUsers(),
        )
    }
}