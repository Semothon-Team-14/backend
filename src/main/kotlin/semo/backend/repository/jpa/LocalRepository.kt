package semo.backend.repository.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import semo.backend.entity.Local

@Repository
interface LocalRepository : JpaRepository<Local, Long> {
    fun findAllByUserIdOrderByCreatedDateTimeDesc(userId: Long): List<Local>

    fun findByIdAndUserId(localId: Long, userId: Long): Local?

    fun existsByUserIdAndCityId(userId: Long, cityId: Long): Boolean

    fun existsByUserIdAndCityIdAndIdNot(userId: Long, cityId: Long, localId: Long): Boolean

    fun deleteAllByUserId(userId: Long)

    @Query(
        """
        select distinct local.user.id
        from Local local
        where local.city.id = :cityId
          and local.user.id <> :excludeUserId
        """,
    )
    fun findDistinctUserIdsByCityIdExcludingUserId(
        @Param("cityId") cityId: Long,
        @Param("excludeUserId") excludeUserId: Long,
    ): List<Long>
}
