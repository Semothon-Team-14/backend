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
import semo.backend.controller.request.CreateRestaurantRequest
import semo.backend.controller.request.UpdateRestaurantRequest
import semo.backend.controller.response.CreateRestaurantResponse
import semo.backend.controller.response.DeleteRestaurantResponse
import semo.backend.controller.response.GetRestaurantResponse
import semo.backend.controller.response.GetRestaurantsResponse
import semo.backend.controller.response.UpdateRestaurantResponse
import semo.backend.facade.RestaurantFacade

@RestController
@RequestMapping("/restaurants/cities/{cityId}")
class RestaurantController(
    private val restaurantFacade: RestaurantFacade,
) {
    @GetMapping
    fun getRestaurants(
        @PathVariable cityId: Long,
    ): GetRestaurantsResponse {
        return GetRestaurantsResponse(
            restaurants = restaurantFacade.getRestaurants(cityId),
        )
    }

    @GetMapping("/{restaurantId}")
    fun getRestaurant(
        @PathVariable cityId: Long,
        @PathVariable restaurantId: Long,
    ): GetRestaurantResponse {
        return GetRestaurantResponse(
            restaurant = restaurantFacade.getRestaurant(cityId, restaurantId),
        )
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createRestaurant(
        @PathVariable cityId: Long,
        @RequestBody request: CreateRestaurantRequest,
    ): CreateRestaurantResponse {
        return CreateRestaurantResponse(
            restaurant = restaurantFacade.createRestaurant(cityId, request),
        )
    }

    @PutMapping("/{restaurantId}")
    fun updateRestaurant(
        @PathVariable cityId: Long,
        @PathVariable restaurantId: Long,
        @OptionalRequestBody request: UpdateRestaurantRequest,
    ): UpdateRestaurantResponse {
        return UpdateRestaurantResponse(
            restaurant = restaurantFacade.updateRestaurant(cityId, restaurantId, request),
        )
    }

    @DeleteMapping("/{restaurantId}")
    fun deleteRestaurant(
        @PathVariable cityId: Long,
        @PathVariable restaurantId: Long,
    ): DeleteRestaurantResponse {
        return DeleteRestaurantResponse(
            deletedRestaurantId = restaurantFacade.deleteRestaurant(cityId, restaurantId),
        )
    }
}
