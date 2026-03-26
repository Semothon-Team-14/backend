package semo.backend.repository.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import semo.backend.entity.RestaurantImage

@Repository
interface RestaurantImageRepository : JpaRepository<RestaurantImage, Long> {
    fun findAllByRestaurantIdOrderByIdAsc(restaurantId: Long): List<RestaurantImage>

    fun findByIdAndRestaurantId(
        id: Long,
        restaurantId: Long,
    ): RestaurantImage?

    fun findAllByRestaurantIdAndIdNot(
        restaurantId: Long,
        id: Long,
    ): List<RestaurantImage>
}
