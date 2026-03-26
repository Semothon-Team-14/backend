package semo.backend.facade

import org.springframework.stereotype.Service
import semo.backend.controller.request.CreateRestaurantRequest
import semo.backend.controller.request.UpdateRestaurantRequest
import semo.backend.dto.RestaurantDto
import semo.backend.service.RestaurantService

@Service
class RestaurantFacade(
    private val restaurantService: RestaurantService,
) {
    fun getRestaurants(cityId: Long): List<RestaurantDto> {
        return restaurantService.getRestaurants(cityId)
    }

    fun getRestaurant(cityId: Long, restaurantId: Long): RestaurantDto {
        return restaurantService.getRestaurant(cityId, restaurantId)
    }

    fun createRestaurant(cityId: Long, request: CreateRestaurantRequest): RestaurantDto {
        return restaurantService.createRestaurant(cityId, request)
    }

    fun updateRestaurant(cityId: Long, restaurantId: Long, request: UpdateRestaurantRequest): RestaurantDto {
        return restaurantService.updateRestaurant(cityId, restaurantId, request)
    }

    fun deleteRestaurant(cityId: Long, restaurantId: Long): Long {
        return restaurantService.deleteRestaurant(cityId, restaurantId)
    }
}
