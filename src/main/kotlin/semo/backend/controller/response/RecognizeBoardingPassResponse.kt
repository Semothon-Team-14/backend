package semo.backend.controller.response

import semo.backend.dto.TicketTripDraftDto

data class RecognizeBoardingPassResponse(
    val draft: TicketTripDraftDto,
)
