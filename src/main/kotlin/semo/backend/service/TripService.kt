package semo.backend.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import semo.backend.controller.request.CreateTripRequest
import semo.backend.controller.request.UpdateTripRequest
import semo.backend.dto.TripDto
import semo.backend.entity.City
import semo.backend.entity.Trip
import semo.backend.entity.User
import semo.backend.exception.city.CityNotFoundException
import semo.backend.exception.trip.TripDateOverlapException
import semo.backend.exception.trip.TripNotFoundException
import semo.backend.exception.user.UserNotFoundException
import semo.backend.mapstruct.TripMapStruct
import semo.backend.repository.jpa.CityRepository
import semo.backend.repository.jpa.TripRepository
import semo.backend.repository.jpa.UserRepository
import semo.backend.util.applyIfProvided
import java.time.LocalDate

@Service
@Transactional(readOnly = true)
class TripService(
    private val tripRepository: TripRepository,
    private val userRepository: UserRepository,
    private val cityRepository: CityRepository,
    private val tripMapStruct: TripMapStruct,
) {
    fun getTrips(): List<TripDto> {
        return tripMapStruct.toDtos(tripRepository.findAll())
    }

    fun getTrip(tripId: Long): TripDto {
        return tripMapStruct.toDto(findTripById(tripId))
    }

    @Transactional
    fun createTrip(userId: Long, request: CreateTripRequest): TripDto {
        val trip = tripMapStruct.toEntity(request)
        val user = findUserById(userId)
        validateNoDateOverlap(user.id, request.startDate, request.endDate)
        trip.user = user
        trip.city = findCityById(request.cityId)
        trip.fromCity = request.fromCityId?.let(::findCityById)
        return tripMapStruct.toDto(tripRepository.save(trip))
    }

    @Transactional
    fun updateTrip(tripId: Long, request: UpdateTripRequest): TripDto {
        val trip = findTripById(tripId)
        var nextStartDate = trip.startDate
        var nextEndDate = trip.endDate
        var nextUserId = trip.user?.id
        request.startDate.applyIfProvided { nextStartDate = it }
        request.endDate.applyIfProvided { nextEndDate = it }
        request.userId.applyIfProvided { nextUserId = it }
        if (nextUserId != null && nextStartDate != null && nextEndDate != null) {
            validateNoDateOverlap(
                userId = nextUserId!!,
                startDate = nextStartDate!!,
                endDate = nextEndDate!!,
                excludeTripId = trip.id,
            )
        }
        request.title.applyIfProvided { trip.title = it }
        request.startDate.applyIfProvided { trip.startDate = it }
        request.endDate.applyIfProvided { trip.endDate = it }
        request.departureDateTime.applyIfProvided { trip.departureDateTime = it }
        request.departureLandingDateTime.applyIfProvided { trip.departureLandingDateTime = it }
        request.userId.applyIfProvided { userId -> trip.user = userId?.let(::findUserById) }
        request.cityId.applyIfProvided { cityId -> trip.city = cityId?.let(::findCityById) }
        request.fromCityId.applyIfProvided { cityId -> trip.fromCity = cityId?.let(::findCityById) }
        return tripMapStruct.toDto(tripRepository.save(trip))
    }

    @Transactional
    fun deleteTrip(tripId: Long): Long {
        val trip = findTripById(tripId)
        tripRepository.delete(trip)
        return tripId
    }

    private fun findTripById(tripId: Long): Trip {
        return tripRepository.findById(tripId)
            .orElseThrow { TripNotFoundException(tripId) }
    }

    private fun findUserById(userId: Long): User {
        return userRepository.findById(userId)
            .orElseThrow { UserNotFoundException(userId) }
    }

    private fun findCityById(cityId: Long): City {
        return cityRepository.findById(cityId)
            .orElseThrow { CityNotFoundException(cityId) }
    }

    private fun validateNoDateOverlap(
        userId: Long,
        startDate: LocalDate,
        endDate: LocalDate,
        excludeTripId: Long? = null,
    ) {
        if (tripRepository.existsDateOverlapByUserId(userId, startDate, endDate, excludeTripId)) {
            throw TripDateOverlapException(userId, startDate, endDate)
        }
    }
}
