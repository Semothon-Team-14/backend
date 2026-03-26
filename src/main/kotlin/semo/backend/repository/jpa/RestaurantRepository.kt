package semo.backend.repository.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import semo.backend.entity.Restaurant

@Repository
interface RestaurantRepository : JpaRepository<Restaurant, Long> {
    fun findAllByCityIdOrderByNameAsc(cityId: Long): List<Restaurant>

    fun findByIdAndCityId(
        id: Long,
        cityId: Long,
    ): Restaurant?
}
