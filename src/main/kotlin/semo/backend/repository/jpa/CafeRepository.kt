package semo.backend.repository.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import semo.backend.entity.Cafe

@Repository
interface CafeRepository : JpaRepository<Cafe, Long> {
    fun findAllByCityIdOrderByNameAsc(cityId: Long): List<Cafe>

    fun findByIdAndCityId(
        id: Long,
        cityId: Long,
    ): Cafe?
}
