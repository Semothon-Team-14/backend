package semo.backend.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import semo.backend.controller.request.CreateTripRequest
import semo.backend.controller.request.PatchValue
import semo.backend.controller.request.UpdateTripRequest
import semo.backend.dto.TripDto
import semo.backend.entity.City
import semo.backend.entity.Trip
import semo.backend.entity.User
import semo.backend.exception.city.CityNotFoundException
import semo.backend.exception.trip.TripNotFoundException
import semo.backend.exception.user.UserNotFoundException
import semo.backend.mapstruct.TripMapStruct
import semo.backend.repository.jpa.CityRepository
import semo.backend.repository.jpa.TripRepository
import semo.backend.repository.jpa.UserRepository

@Service
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
        trip.user = findUserById(userId)
        trip.city = findCityById(request.cityId)
        return tripMapStruct.toDto(tripRepository.save(trip))
    }

    @Transactional
    fun updateTrip(tripId: Long, request: UpdateTripRequest): TripDto {
        val trip = findTripById(tripId)
        request.title.ifPresent { trip.title = it }
        request.startDate.ifPresent { trip.startDate = it }
        request.endDate.ifPresent { trip.endDate = it }
        request.userId.ifPresent { userId -> trip.user = userId?.let(::findUserById) }
        request.cityId.ifPresent { cityId -> trip.city = cityId?.let(::findCityById) }
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

    private inline fun <T> PatchValue<T>.ifPresent(
        block: (T?) -> Unit,
    ) {
        if (present) {
            block(value)
        }
    }
}
