package semo.backend.facade

import org.springframework.stereotype.Service
import semo.backend.controller.request.CreateSavedRestaurantRequest
import semo.backend.controller.request.UpdateSavedRestaurantRequest
import semo.backend.dto.SavedRestaurantDto
import semo.backend.service.SavedRestaurantService

@Service
class SavedRestaurantFacade(
    private val savedRestaurantService: SavedRestaurantService,
) {
    fun getSavedRestaurants(userId: Long): List<SavedRestaurantDto> {
        return savedRestaurantService.getSavedRestaurants(userId)
    }

    fun getSavedRestaurant(userId: Long, savedRestaurantId: Long): SavedRestaurantDto {
        return savedRestaurantService.getSavedRestaurant(userId, savedRestaurantId)
    }

    fun createSavedRestaurant(userId: Long, request: CreateSavedRestaurantRequest): SavedRestaurantDto {
        return savedRestaurantService.createSavedRestaurant(userId, request)
    }

    fun updateSavedRestaurant(
        userId: Long,
        savedRestaurantId: Long,
        request: UpdateSavedRestaurantRequest,
    ): SavedRestaurantDto {
        return savedRestaurantService.updateSavedRestaurant(userId, savedRestaurantId, request)
    }

    fun deleteSavedRestaurant(userId: Long, savedRestaurantId: Long): Long {
        return savedRestaurantService.deleteSavedRestaurant(userId, savedRestaurantId)
    }
}
