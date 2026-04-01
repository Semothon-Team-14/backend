package semo.backend.facade

import org.springframework.stereotype.Service
import semo.backend.controller.request.CreateMingleRequest
import semo.backend.controller.request.UpdateMingleRequest
import semo.backend.dto.MingleDto
import semo.backend.service.MingleService

@Service
class MingleFacade(
    private val mingleService: MingleService,
) {
    fun getMingles(): List<MingleDto> {
        return mingleService.getMingles()
    }

    fun getMingle(mingleId: Long): MingleDto {
        return mingleService.getMingle(mingleId)
    }

    fun createMingle(request: CreateMingleRequest): MingleDto {
        return mingleService.createMingle(request)
    }

    fun updateMingle(mingleId: Long, request: UpdateMingleRequest): MingleDto {
        return mingleService.updateMingle(mingleId, request)
    }

    fun deleteMingle(mingleId: Long): Long {
        return mingleService.deleteMingle(mingleId)
    }
}
