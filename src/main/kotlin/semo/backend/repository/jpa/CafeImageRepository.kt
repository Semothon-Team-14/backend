package semo.backend.repository.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import semo.backend.entity.CafeImage

@Repository
interface CafeImageRepository : JpaRepository<CafeImage, Long> {
    fun findAllByCafeIdOrderByIdAsc(cafeId: Long): List<CafeImage>

    fun findByIdAndCafeId(
        id: Long,
        cafeId: Long,
    ): CafeImage?

    fun findAllByCafeIdAndIdNot(
        cafeId: Long,
        id: Long,
    ): List<CafeImage>
}
