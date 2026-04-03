package semo.backend.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import semo.backend.controller.request.RecognizeBoardingPassRequest
import semo.backend.controller.response.RecognizeBoardingPassResponse
import semo.backend.facade.TicketValidationFacade

@RestController
@RequestMapping("/ticket-validations")
class TicketValidationController(
    private val ticketValidationFacade: TicketValidationFacade,
) {
    @PostMapping("/boarding-passes")
    fun recognizeBoardingPass(
        @RequestBody request: RecognizeBoardingPassRequest,
    ): RecognizeBoardingPassResponse {
        return RecognizeBoardingPassResponse(
            draft = ticketValidationFacade.recognizeBoardingPass(request),
        )
    }
}
