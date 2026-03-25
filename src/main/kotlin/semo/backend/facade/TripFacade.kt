package semo.backend.facade

import org.springframework.stereotype.Service
import semo.backend.controller.request.CreateTripRequest
import semo.backend.controller.request.UpdateTripRequest
import semo.backend.dto.TripDto
import semo.backend.service.TripService

@Service
class TripFacade(
    private val tripService: TripService,
) {
    fun getTrips(): List<TripDto> {
        return tripService.getTrips()
    }

    fun getTrip(tripId: Long): TripDto {
        return tripService.getTrip(tripId)
    }

    fun createTrip(request: CreateTripRequest): TripDto {
        return tripService.createTrip(request)
    }

    fun updateTrip(tripId: Long, request: UpdateTripRequest): TripDto {
        return tripService.updateTrip(tripId, request)
    }

    fun deleteTrip(tripId: Long): Long {
        return tripService.deleteTrip(tripId)
    }
}
