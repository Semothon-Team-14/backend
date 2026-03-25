package semo.backend.repository.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import semo.backend.entity.Nationality

@Repository
interface NationalityRepository : JpaRepository<Nationality, Long> {
    fun existsByCountryCode(countryCode: String): Boolean

    fun existsByCountryCodeAndIdNot(
        countryCode: String,
        id: Long,
    ): Boolean
}
