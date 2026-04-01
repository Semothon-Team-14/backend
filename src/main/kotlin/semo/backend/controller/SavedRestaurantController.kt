package semo.backend.controller

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
import semo.backend.controller.request.CreateSavedRestaurantRequest
import semo.backend.controller.request.UpdateSavedRestaurantRequest
import semo.backend.controller.response.CreateSavedRestaurantResponse
import semo.backend.controller.response.DeleteSavedRestaurantResponse
import semo.backend.controller.response.GetSavedRestaurantResponse
import semo.backend.controller.response.GetSavedRestaurantsResponse
import semo.backend.controller.response.UpdateSavedRestaurantResponse
import semo.backend.facade.SavedRestaurantFacade
import semo.backend.security.UserId

@RestController
@RequestMapping("/saved-restaurants")
class SavedRestaurantController(
    private val savedRestaurantFacade: SavedRestaurantFacade,
) {
    @GetMapping
    fun getSavedRestaurants(
        @UserId userId: Long,
    ): GetSavedRestaurantsResponse {
        return GetSavedRestaurantsResponse(
            savedRestaurants = savedRestaurantFacade.getSavedRestaurants(userId),
        )
    }

    @GetMapping("/{savedRestaurantId}")
    fun getSavedRestaurant(
        @UserId userId: Long,
        @PathVariable savedRestaurantId: Long,
    ): GetSavedRestaurantResponse {
        return GetSavedRestaurantResponse(
            savedRestaurant = savedRestaurantFacade.getSavedRestaurant(userId, savedRestaurantId),
        )
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createSavedRestaurant(
        @UserId userId: Long,
        @RequestBody request: CreateSavedRestaurantRequest,
    ): CreateSavedRestaurantResponse {
        return CreateSavedRestaurantResponse(
            savedRestaurant = savedRestaurantFacade.createSavedRestaurant(userId, request),
        )
    }

    @PutMapping("/{savedRestaurantId}")
    fun updateSavedRestaurant(
        @UserId userId: Long,
        @PathVariable savedRestaurantId: Long,
        @OptionalRequestBody request: UpdateSavedRestaurantRequest,
    ): UpdateSavedRestaurantResponse {
        return UpdateSavedRestaurantResponse(
            savedRestaurant = savedRestaurantFacade.updateSavedRestaurant(userId, savedRestaurantId, request),
        )
    }

    @DeleteMapping("/{savedRestaurantId}")
    fun deleteSavedRestaurant(
        @UserId userId: Long,
        @PathVariable savedRestaurantId: Long,
    ): DeleteSavedRestaurantResponse {
        return DeleteSavedRestaurantResponse(
            deletedSavedRestaurantId = savedRestaurantFacade.deleteSavedRestaurant(userId, savedRestaurantId),
        )
    }
}
