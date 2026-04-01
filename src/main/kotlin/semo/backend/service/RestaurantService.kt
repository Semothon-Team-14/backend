package semo.backend.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import semo.backend.controller.request.CreateRestaurantRequest
import semo.backend.controller.request.UpdateRestaurantRequest
import semo.backend.dto.RestaurantDto
import semo.backend.entity.City
import semo.backend.entity.Restaurant
import semo.backend.exception.city.CityNotFoundException
import semo.backend.exception.restaurant.RestaurantNotFoundException
import semo.backend.mapstruct.RestaurantMapStruct
import semo.backend.repository.jpa.CityRepository
import semo.backend.repository.jpa.RestaurantImageRepository
import semo.backend.repository.jpa.RestaurantRepository
import semo.backend.repository.jpa.SavedRestaurantRepository
import semo.backend.util.applyIfProvided

@Service
class RestaurantService(
    private val restaurantRepository: RestaurantRepository,
    private val cityRepository: CityRepository,
    private val restaurantImageRepository: RestaurantImageRepository,
    private val restaurantMapStruct: RestaurantMapStruct,
    private val savedRestaurantRepository: SavedRestaurantRepository,
) {
    fun getRestaurants(cityId: Long): List<RestaurantDto> {
        findCityById(cityId)
        return restaurantMapStruct.toDtos(restaurantRepository.findAllByCityIdOrderByNameAsc(cityId))
    }

    fun getRestaurant(cityId: Long, restaurantId: Long): RestaurantDto {
        return restaurantMapStruct.toDto(findRestaurantByCityIdAndRestaurantId(cityId, restaurantId))
    }

    @Transactional
    fun createRestaurant(cityId: Long, request: CreateRestaurantRequest): RestaurantDto {
        val city = findCityById(cityId)
        val restaurant = Restaurant(
            name = request.name.trim(),
            phoneNumber = request.phoneNumber?.trim(),
            address = request.address?.trim(),
            foodCategory = request.foodCategory?.trim(),
            latitude = request.latitude,
            longitude = request.longitude,
            city = city,
        )
        return restaurantMapStruct.toDto(restaurantRepository.save(restaurant))
    }

    @Transactional
    fun updateRestaurant(cityId: Long, restaurantId: Long, request: UpdateRestaurantRequest): RestaurantDto {
        val restaurant = findRestaurantByCityIdAndRestaurantId(cityId, restaurantId)
        request.name.applyIfProvided { name -> restaurant.name = name?.trim() ?: restaurant.name }
        request.phoneNumber.applyIfProvided { phoneNumber -> restaurant.phoneNumber = phoneNumber?.trim() }
        request.address.applyIfProvided { address -> restaurant.address = address?.trim() }
        request.foodCategory.applyIfProvided { foodCategory -> restaurant.foodCategory = foodCategory?.trim() }
        request.latitude.applyIfProvided { latitude -> restaurant.latitude = latitude }
        request.longitude.applyIfProvided { longitude -> restaurant.longitude = longitude }
        return restaurantMapStruct.toDto(restaurantRepository.save(restaurant))
    }

    @Transactional
    fun deleteRestaurant(cityId: Long, restaurantId: Long): Long {
        val restaurant = findRestaurantByCityIdAndRestaurantId(cityId, restaurantId)
        savedRestaurantRepository.deleteAllByRestaurantId(restaurantId)
        val images = restaurantImageRepository.findAllByRestaurantIdOrderByIdAsc(restaurantId)
        if (images.isNotEmpty()) {
            restaurantImageRepository.deleteAll(images)
        }
        restaurantRepository.delete(restaurant)
        return restaurantId
    }

    fun findRestaurantById(restaurantId: Long): Restaurant {
        return restaurantRepository.findById(restaurantId)
            .orElseThrow { RestaurantNotFoundException(restaurantId) }
    }

    private fun findCityById(cityId: Long): City {
        return cityRepository.findById(cityId)
            .orElseThrow { CityNotFoundException(cityId) }
    }

    private fun findRestaurantByCityIdAndRestaurantId(cityId: Long, restaurantId: Long): Restaurant {
        findCityById(cityId)
        return restaurantRepository.findByIdAndCityId(restaurantId, cityId)
            ?: throw RestaurantNotFoundException(restaurantId)
    }
}
