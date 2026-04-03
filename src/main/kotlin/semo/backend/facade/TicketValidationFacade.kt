package semo.backend.facade

import org.springframework.stereotype.Service
import semo.backend.controller.request.RecognizeBoardingPassRequest
import semo.backend.dto.TicketTripDraftDto
import semo.backend.service.TicketValidationService

@Service
class TicketValidationFacade(
    private val ticketValidationService: TicketValidationService,
) {
    fun recognizeBoardingPass(request: RecognizeBoardingPassRequest): TicketTripDraftDto {
        return ticketValidationService.recognizeBoardingPass(request)
    }
}
