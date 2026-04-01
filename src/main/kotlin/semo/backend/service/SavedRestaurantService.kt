package semo.backend.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import semo.backend.controller.request.CreateSavedRestaurantRequest
import semo.backend.controller.request.UpdateSavedRestaurantRequest
import semo.backend.dto.SavedRestaurantDto
import semo.backend.entity.Restaurant
import semo.backend.entity.SavedRestaurant
import semo.backend.entity.User
import semo.backend.exception.restaurant.RestaurantNotFoundException
import semo.backend.exception.savedrestaurant.SavedRestaurantDuplicateException
import semo.backend.exception.savedrestaurant.SavedRestaurantNotFoundException
import semo.backend.exception.savedrestaurant.SavedRestaurantTargetRequiredException
import semo.backend.exception.user.UserNotFoundException
import semo.backend.mapstruct.SavedRestaurantMapStruct
import semo.backend.repository.jpa.RestaurantRepository
import semo.backend.repository.jpa.SavedRestaurantRepository
import semo.backend.repository.jpa.UserRepository
import semo.backend.util.applyIfProvided

@Service
@Transactional(readOnly = true)
class SavedRestaurantService(
    private val savedRestaurantRepository: SavedRestaurantRepository,
    private val userRepository: UserRepository,
    private val restaurantRepository: RestaurantRepository,
    private val savedRestaurantMapStruct: SavedRestaurantMapStruct,
) {
    fun getSavedRestaurants(userId: Long): List<SavedRestaurantDto> {
        findUserById(userId)
        return savedRestaurantMapStruct.toDtos(savedRestaurantRepository.findAllByUserIdOrderByCreatedDateTimeDesc(userId))
    }

    fun getSavedRestaurant(userId: Long, savedRestaurantId: Long): SavedRestaurantDto {
        return savedRestaurantMapStruct.toDto(findSavedRestaurant(userId, savedRestaurantId))
    }

    @Transactional
    fun createSavedRestaurant(userId: Long, request: CreateSavedRestaurantRequest): SavedRestaurantDto {
        val user = findUserById(userId)
        val restaurant = findRestaurantById(request.restaurantId)
        if (savedRestaurantRepository.existsByUserIdAndRestaurantId(userId, restaurant.id)) {
            throw SavedRestaurantDuplicateException(userId, restaurant.id)
        }
        val savedRestaurant = SavedRestaurant(
            user = user,
            restaurant = restaurant,
        )
        return savedRestaurantMapStruct.toDto(savedRestaurantRepository.save(savedRestaurant))
    }

    @Transactional
    fun updateSavedRestaurant(userId: Long, savedRestaurantId: Long, request: UpdateSavedRestaurantRequest): SavedRestaurantDto {
        val savedRestaurant = findSavedRestaurant(userId, savedRestaurantId)
        request.restaurantId.applyIfProvided { restaurantId ->
            val nextRestaurantId = restaurantId ?: throw SavedRestaurantTargetRequiredException()
            if (savedRestaurantRepository.existsByUserIdAndRestaurantIdAndIdNot(userId, nextRestaurantId, savedRestaurantId)) {
                throw SavedRestaurantDuplicateException(userId, nextRestaurantId)
            }
            savedRestaurant.restaurant = findRestaurantById(nextRestaurantId)
        }
        return savedRestaurantMapStruct.toDto(savedRestaurantRepository.save(savedRestaurant))
    }

    @Transactional
    fun deleteSavedRestaurant(userId: Long, savedRestaurantId: Long): Long {
        val savedRestaurant = findSavedRestaurant(userId, savedRestaurantId)
        savedRestaurantRepository.delete(savedRestaurant)
        return savedRestaurantId
    }

    @Transactional
    fun deleteAllByUserId(userId: Long) {
        savedRestaurantRepository.deleteAllByUserId(userId)
    }

    @Transactional
    fun deleteAllByRestaurantId(restaurantId: Long) {
        savedRestaurantRepository.deleteAllByRestaurantId(restaurantId)
    }

    private fun findSavedRestaurant(userId: Long, savedRestaurantId: Long): SavedRestaurant {
        findUserById(userId)
        return savedRestaurantRepository.findByIdAndUserId(savedRestaurantId, userId)
            ?: throw SavedRestaurantNotFoundException(savedRestaurantId)
    }

    private fun findUserById(userId: Long): User {
        return userRepository.findById(userId)
            .orElseThrow { UserNotFoundException(userId) }
    }

    private fun findRestaurantById(restaurantId: Long): Restaurant {
        return restaurantRepository.findById(restaurantId)
            .orElseThrow { RestaurantNotFoundException(restaurantId) }
    }
}
