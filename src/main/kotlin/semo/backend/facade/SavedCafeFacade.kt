package semo.backend.facade

import org.springframework.stereotype.Service
import semo.backend.controller.request.CreateSavedCafeRequest
import semo.backend.controller.request.UpdateSavedCafeRequest
import semo.backend.dto.SavedCafeDto
import semo.backend.service.SavedCafeService

@Service
class SavedCafeFacade(
    private val savedCafeService: SavedCafeService,
) {
    fun getSavedCafes(userId: Long): List<SavedCafeDto> {
        return savedCafeService.getSavedCafes(userId)
    }

    fun getSavedCafe(userId: Long, savedCafeId: Long): SavedCafeDto {
        return savedCafeService.getSavedCafe(userId, savedCafeId)
    }

    fun createSavedCafe(userId: Long, request: CreateSavedCafeRequest): SavedCafeDto {
        return savedCafeService.createSavedCafe(userId, request)
    }

    fun updateSavedCafe(userId: Long, savedCafeId: Long, request: UpdateSavedCafeRequest): SavedCafeDto {
        return savedCafeService.updateSavedCafe(userId, savedCafeId, request)
    }

    fun deleteSavedCafe(userId: Long, savedCafeId: Long): Long {
        return savedCafeService.deleteSavedCafe(userId, savedCafeId)
    }
}
