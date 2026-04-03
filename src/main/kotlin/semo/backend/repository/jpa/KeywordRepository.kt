package semo.backend.repository.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import semo.backend.entity.Keyword

@Repository
interface KeywordRepository : JpaRepository<Keyword, Long> {
    fun findAllByOrderByPriorityAscLabelAsc(): List<Keyword>

    fun existsByLabel(label: String): Boolean

    fun existsByLabelEnglish(labelEnglish: String): Boolean

    fun existsByLabelAndIdNot(
        label: String,
        id: Long,
    ): Boolean

    fun existsByLabelEnglishAndIdNot(
        labelEnglish: String,
        id: Long,
    ): Boolean

    @Query(
        """
        select keyword
        from Keyword keyword
        where lower(keyword.label) like lower(concat('%', :query, '%'))
           or lower(keyword.labelEnglish) like lower(concat('%', :query, '%'))
        order by keyword.priority asc, keyword.label asc
        """,
    )
    fun searchByLabelOrderByPriority(query: String): List<Keyword>
}
