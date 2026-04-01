package semo.backend.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import semo.backend.controller.request.CreateQuickMatchRequest
import semo.backend.controller.response.AcceptQuickMatchResponse
import semo.backend.controller.response.CreateQuickMatchResponse
import semo.backend.controller.response.DeclineQuickMatchResponse
import semo.backend.controller.response.GetQuickMatchResponse
import semo.backend.controller.response.GetQuickMatchesResponse
import semo.backend.enums.QuickMatchTargetType
import semo.backend.facade.QuickMatchFacade
import semo.backend.security.UserId

@RestController
@RequestMapping("/quick-matches")
class QuickMatchController(
    private val quickMatchFacade: QuickMatchFacade,
) {
    @GetMapping
    fun getQuickMatches(
        @RequestParam(required = false) cityId: Long?,
        @RequestParam(required = false) targetType: QuickMatchTargetType?,
    ): GetQuickMatchesResponse {
        return GetQuickMatchesResponse(
            quickMatches = quickMatchFacade.getQuickMatches(cityId, targetType),
        )
    }

    @GetMapping("/{quickMatchId}")
    fun getQuickMatch(
        @PathVariable quickMatchId: Long,
    ): GetQuickMatchResponse {
        return GetQuickMatchResponse(
            quickMatch = quickMatchFacade.getQuickMatch(quickMatchId),
        )
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createQuickMatch(
        @UserId userId: Long,
        @RequestBody request: CreateQuickMatchRequest,
    ): CreateQuickMatchResponse {
        return CreateQuickMatchResponse(
            quickMatch = quickMatchFacade.createQuickMatch(userId, request),
        )
    }

    @PostMapping("/{quickMatchId}/accept")
    fun acceptQuickMatch(
        @UserId userId: Long,
        @PathVariable quickMatchId: Long,
    ): AcceptQuickMatchResponse {
        return AcceptQuickMatchResponse(
            result = quickMatchFacade.acceptQuickMatch(userId, quickMatchId),
        )
    }

    @PostMapping("/{quickMatchId}/decline")
    fun declineQuickMatch(
        @UserId userId: Long,
        @PathVariable quickMatchId: Long,
    ): DeclineQuickMatchResponse {
        return DeclineQuickMatchResponse(
            quickMatch = quickMatchFacade.declineQuickMatch(userId, quickMatchId),
        )
    }
}
