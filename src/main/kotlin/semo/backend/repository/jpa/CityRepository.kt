package semo.backend.repository.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import semo.backend.entity.City

@Repository
interface CityRepository : JpaRepository<City, Long> {
    fun findAllByNationalityIdOrderByCityNameEnglishAsc(nationalityId: Long): List<City>

    fun findByIdAndNationalityId(
        id: Long,
        nationalityId: Long,
    ): City?

    fun existsByNationalityIdAndCityNameEnglish(
        nationalityId: Long,
        cityNameEnglish: String,
    ): Boolean

    fun existsByNationalityIdAndCityNameEnglishAndIdNot(
        nationalityId: Long,
        cityNameEnglish: String,
        id: Long,
    ): Boolean
}
