package semo.backend.repository.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import semo.backend.entity.Mingle

@Repository
interface MingleRepository : JpaRepository<Mingle, Long> {
    fun findAllByCityIdOrderByCreatedDateTimeDesc(cityId: Long): List<Mingle>
}
