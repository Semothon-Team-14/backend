package semo.backend.repository.jpa

import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import semo.backend.entity.Trip
import java.time.LocalDate

@Repository
interface TripRepository : JpaRepository<Trip, Long> {
    @Query(
        """
        select distinct trip.user.id
        from Trip trip
        where trip.city.id = :cityId
          and trip.user.id is not null
          and trip.user.id <> :excludeUserId
          and (trip.startDate is null or trip.startDate <= :targetDate)
          and (trip.endDate is null or trip.endDate >= :targetDate)
        """,
    )
    fun findActiveTravelerUserIdsByCityId(
        @Param("cityId") cityId: Long,
        @Param("targetDate") targetDate: LocalDate,
        @Param("excludeUserId") excludeUserId: Long,
    ): List<Long>

    @Query(
        """
        select (count(trip) > 0)
        from Trip trip
        where trip.user.id = :userId
          and (:excludeTripId is null or trip.id <> :excludeTripId)
          and trip.startDate is not null
          and trip.endDate is not null
          and trip.startDate <= :endDate
          and trip.endDate >= :startDate
        """,
    )
    fun existsDateOverlapByUserId(
        @Param("userId") userId: Long,
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate,
        @Param("excludeTripId") excludeTripId: Long? = null,
    ): Boolean
}
