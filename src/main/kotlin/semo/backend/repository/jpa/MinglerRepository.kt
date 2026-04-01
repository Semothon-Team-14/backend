package semo.backend.repository.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import semo.backend.entity.Mingler

@Repository
interface MinglerRepository : JpaRepository<Mingler, Long> {
    fun findAllByUserIdOrderByCreatedDateTimeDesc(userId: Long): List<Mingler>

    fun findByIdAndUserId(minglerId: Long, userId: Long): Mingler?

    fun existsByMingleIdAndUserId(mingleId: Long, userId: Long): Boolean

    fun existsByMingleIdAndUserIdAndIdNot(mingleId: Long, userId: Long, minglerId: Long): Boolean

    fun deleteAllByUserId(userId: Long)

    fun deleteAllByMingleId(mingleId: Long)
}
