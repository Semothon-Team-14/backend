package semo.backend.controller.response

import semo.backend.dto.TripDto

data class GetTripsResponse(
    val trips: List<TripDto>,
)
