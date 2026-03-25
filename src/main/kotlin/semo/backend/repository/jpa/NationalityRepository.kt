package semo.backend.repository.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import semo.backend.entity.Nationality

@Repository
interface NationalityRepository : JpaRepository<Nationality, Long> {
    fun findAllByOrderByCountryNameEnglishAsc(): List<Nationality>

    fun existsByCountryCode(countryCode: String): Boolean

    fun existsByCountryCodeAndIdNot(
        countryCode: String,
        id: Long,
    ): Boolean

    @Query(
        """
        select nationality
        from Nationality nationality
        where lower(nationality.countryCode) like lower(concat('%', :query, '%'))
           or lower(nationality.countryNameEnglish) like lower(concat('%', :query, '%'))
           or nationality.countryNameKorean like concat('%', :query, '%')
        order by nationality.countryNameEnglish asc
        """,
    )
    fun search(query: String): List<Nationality>
}
