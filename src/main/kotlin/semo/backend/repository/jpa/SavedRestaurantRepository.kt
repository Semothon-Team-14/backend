package semo.backend.repository.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import semo.backend.entity.SavedRestaurant

@Repository
interface SavedRestaurantRepository : JpaRepository<SavedRestaurant, Long> {
    fun findAllByUserIdOrderByCreatedDateTimeDesc(userId: Long): List<SavedRestaurant>

    fun findByIdAndUserId(savedRestaurantId: Long, userId: Long): SavedRestaurant?

    fun existsByUserIdAndRestaurantId(userId: Long, restaurantId: Long): Boolean

    fun existsByUserIdAndRestaurantIdAndIdNot(userId: Long, restaurantId: Long, savedRestaurantId: Long): Boolean

    fun deleteAllByUserId(userId: Long)

    fun deleteAllByRestaurantId(restaurantId: Long)
}
