package semo.backend.controller.response

import semo.backend.dto.QuickMatchDto

data class GetQuickMatchesResponse(
    val quickMatches: List<QuickMatchDto>,
)
