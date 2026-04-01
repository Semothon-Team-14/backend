package semo.backend.repository.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import semo.backend.entity.QuickMatchResponse

@Repository
interface QuickMatchResponseRepository : JpaRepository<QuickMatchResponse, Long> {
    fun findByQuickMatchIdAndResponderUserId(quickMatchId: Long, responderUserId: Long): QuickMatchResponse?

    fun existsByQuickMatchIdAndResponderUserId(quickMatchId: Long, responderUserId: Long): Boolean

    fun deleteAllByResponderUserId(responderUserId: Long)

    fun deleteAllByQuickMatchId(quickMatchId: Long)

    fun deleteAllByQuickMatchIdIn(quickMatchIds: List<Long>)
}
