package semo.backend.facade

import org.springframework.stereotype.Service
import semo.backend.controller.request.CreateQuickMatchRequest
import semo.backend.dto.QuickMatchAcceptResultDto
import semo.backend.dto.QuickMatchDto
import semo.backend.service.QuickMatchService

@Service
class QuickMatchFacade(
    private val quickMatchService: QuickMatchService,
) {
    fun getQuickMatches(cityId: Long?): List<QuickMatchDto> {
        return quickMatchService.getQuickMatches(cityId)
    }

    fun getQuickMatch(quickMatchId: Long): QuickMatchDto {
        return quickMatchService.getQuickMatch(quickMatchId)
    }

    fun createQuickMatch(userId: Long, request: CreateQuickMatchRequest): QuickMatchDto {
        return quickMatchService.createQuickMatch(userId, request)
    }

    fun acceptQuickMatch(userId: Long, quickMatchId: Long): QuickMatchAcceptResultDto {
        return quickMatchService.acceptQuickMatch(userId, quickMatchId)
    }

    fun declineQuickMatch(userId: Long, quickMatchId: Long): QuickMatchDto {
        return quickMatchService.declineQuickMatch(userId, quickMatchId)
    }
}
