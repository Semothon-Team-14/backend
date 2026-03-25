package semo.backend.repository.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import semo.backend.entity.Keyword

@Repository
interface KeywordRepository : JpaRepository<Keyword, Long> {
    fun existsByLabel(label: String): Boolean

    fun existsByLabelAndIdNot(
        label: String,
        id: Long,
    ): Boolean
}
