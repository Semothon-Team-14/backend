package semo.backend.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import semo.backend.controller.request.CreateTripRequest
import semo.backend.controller.request.UpdateTripRequest
import semo.backend.controller.response.CreateTripResponse
import semo.backend.controller.response.DeleteTripResponse
import semo.backend.controller.response.GetTripResponse
import semo.backend.controller.response.GetTripsResponse
import semo.backend.controller.response.UpdateTripResponse
import semo.backend.facade.TripFacade

@RestController
@RequestMapping("/trips")
class TripController(
    private val tripFacade: TripFacade,
) {
    @GetMapping
    fun getTrips(): GetTripsResponse {
        return GetTripsResponse(
            trips = tripFacade.getTrips(),
        )
    }

    @GetMapping("/{tripId}")
    fun getTrip(
        @PathVariable tripId: Long,
    ): GetTripResponse {
        return GetTripResponse(
            trip = tripFacade.getTrip(tripId),
        )
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createTrip(
        @RequestBody request: CreateTripRequest,
    ): CreateTripResponse {
        return CreateTripResponse(
            trip = tripFacade.createTrip(request),
        )
    }

    @PutMapping("/{tripId}")
    fun updateTrip(
        @PathVariable tripId: Long,
        @RequestBody request: UpdateTripRequest,
    ): UpdateTripResponse {
        return UpdateTripResponse(
            trip = tripFacade.updateTrip(tripId, request),
        )
    }

    @DeleteMapping("/{tripId}")
    fun deleteTrip(
        @PathVariable tripId: Long,
    ): DeleteTripResponse {
        return DeleteTripResponse(
            deletedTripId = tripFacade.deleteTrip(tripId),
        )
    }
}
