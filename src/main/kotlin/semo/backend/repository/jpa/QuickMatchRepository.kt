package semo.backend.repository.jpa

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import semo.backend.entity.QuickMatch
import semo.backend.enums.QuickMatchStatus
import semo.backend.enums.QuickMatchTargetType

@Repository
interface QuickMatchRepository : JpaRepository<QuickMatch, Long> {
    @EntityGraph(attributePaths = ["requesterUser", "city", "acceptedByUser", "mingle"])
    fun findAllByCityIdAndStatusOrderByCreatedDateTimeDesc(cityId: Long, status: QuickMatchStatus): List<QuickMatch>

    @EntityGraph(attributePaths = ["requesterUser", "city", "acceptedByUser", "mingle"])
    fun findAllByCityIdAndStatusAndTargetTypeOrderByCreatedDateTimeDesc(
        cityId: Long,
        status: QuickMatchStatus,
        targetType: QuickMatchTargetType,
    ): List<QuickMatch>

    @EntityGraph(attributePaths = ["requesterUser", "city", "acceptedByUser", "mingle"])
    fun findAllByStatusOrderByCreatedDateTimeDesc(status: QuickMatchStatus): List<QuickMatch>

    @EntityGraph(attributePaths = ["requesterUser", "city", "acceptedByUser", "mingle"])
    fun findAllByStatusAndTargetTypeOrderByCreatedDateTimeDesc(
        status: QuickMatchStatus,
        targetType: QuickMatchTargetType,
    ): List<QuickMatch>

    @EntityGraph(attributePaths = ["requesterUser", "city", "acceptedByUser", "mingle"])
    override fun findById(id: Long): java.util.Optional<QuickMatch>

    fun findAllByMingleId(mingleId: Long): List<QuickMatch>

    fun deleteAllByRequesterUserIdOrAcceptedByUserId(requesterUserId: Long, acceptedByUserId: Long)
}
