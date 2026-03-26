package semo.backend.facade

import org.springframework.stereotype.Service
import semo.backend.controller.request.CreateCafeRequest
import semo.backend.controller.request.UpdateCafeRequest
import semo.backend.dto.CafeDto
import semo.backend.service.CafeService

@Service
class CafeFacade(
    private val cafeService: CafeService,
) {
    fun getCafes(cityId: Long): List<CafeDto> {
        return cafeService.getCafes(cityId)
    }

    fun getCafe(cityId: Long, cafeId: Long): CafeDto {
        return cafeService.getCafe(cityId, cafeId)
    }

    fun createCafe(cityId: Long, request: CreateCafeRequest): CafeDto {
        return cafeService.createCafe(cityId, request)
    }

    fun updateCafe(cityId: Long, cafeId: Long, request: UpdateCafeRequest): CafeDto {
        return cafeService.updateCafe(cityId, cafeId, request)
    }

    fun deleteCafe(cityId: Long, cafeId: Long): Long {
        return cafeService.deleteCafe(cityId, cafeId)
    }
}
