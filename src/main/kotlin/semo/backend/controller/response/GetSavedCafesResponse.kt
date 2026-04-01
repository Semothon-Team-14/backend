package semo.backend.controller.response

import semo.backend.dto.SavedCafeDto

data class GetSavedCafesResponse(
    val savedCafes: List<SavedCafeDto>,
)
