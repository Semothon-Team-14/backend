package semo.backend.controller

import org.slf4j.LoggerFactory
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import semo.backend.controller.request.CreateQuickMatchRequest
import semo.backend.exception.BaseCustomException
import semo.backend.facade.QuickMatchFacade

@Controller
class QuickMatchWebSocketController(
    private val quickMatchFacade: QuickMatchFacade,
    private val simpMessagingTemplate: SimpMessagingTemplate,
) {
    @MessageMapping("/quick-matches")
    fun createQuickMatch(
        request: CreateQuickMatchRequest,
        authentication: Authentication,
    ) {
        val userId = authentication.principal.toString().toLong()
        try {
            quickMatchFacade.createQuickMatch(userId, request)
        } catch (exception: BaseCustomException) {
            publishError(userId, "QUICK_MATCH_CREATE", exception.reason)
        }
    }

    @MessageMapping("/quick-matches/{quickMatchId}/accept")
    fun acceptQuickMatch(
        @DestinationVariable quickMatchId: Long,
        authentication: Authentication,
    ) {
        val userId = authentication.principal.toString().toLong()
        try {
            quickMatchFacade.acceptQuickMatch(userId, quickMatchId)
        } catch (exception: BaseCustomException) {
            publishError(userId, "QUICK_MATCH_ACCEPT", exception.reason)
        }
    }

    @MessageMapping("/quick-matches/{quickMatchId}/decline")
    fun declineQuickMatch(
        @DestinationVariable quickMatchId: Long,
        authentication: Authentication,
    ) {
        val userId = authentication.principal.toString().toLong()
        try {
            quickMatchFacade.declineQuickMatch(userId, quickMatchId)
        } catch (exception: BaseCustomException) {
            publishError(userId, "QUICK_MATCH_DECLINE", exception.reason)
        }
    }

    private fun publishError(userId: Long, action: String, reason: String) {
        log.warn("QM WS ACTION FAIL action={} userId={} reason={}", action, userId, reason)
        simpMessagingTemplate.convertAndSend(
            "/topic/users/$userId/quick-matches",
            QuickMatchSocketErrorEvent(
                eventType = "QUICK_MATCH_ERROR",
                action = action,
                reason = reason,
            ),
        )
    }

    data class QuickMatchSocketErrorEvent(
        val eventType: String,
        val action: String,
        val reason: String,
    )

    companion object {
        private val log = LoggerFactory.getLogger(QuickMatchWebSocketController::class.java)
    }
}

