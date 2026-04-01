package semo.backend.repository.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import semo.backend.entity.SavedCafe

@Repository
interface SavedCafeRepository : JpaRepository<SavedCafe, Long> {
    fun findAllByUserIdOrderByCreatedDateTimeDesc(userId: Long): List<SavedCafe>

    fun findByIdAndUserId(savedCafeId: Long, userId: Long): SavedCafe?

    fun existsByUserIdAndCafeId(userId: Long, cafeId: Long): Boolean

    fun existsByUserIdAndCafeIdAndIdNot(userId: Long, cafeId: Long, savedCafeId: Long): Boolean

    fun deleteAllByUserId(userId: Long)

    fun deleteAllByCafeId(cafeId: Long)
}
