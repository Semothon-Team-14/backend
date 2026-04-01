package semo.backend.facade

import org.springframework.stereotype.Service
import semo.backend.controller.request.CreateMinglerRequest
import semo.backend.controller.request.UpdateMinglerRequest
import semo.backend.dto.MinglerDto
import semo.backend.service.MinglerService

@Service
class MinglerFacade(
    private val minglerService: MinglerService,
) {
    fun getMinglers(userId: Long): List<MinglerDto> {
        return minglerService.getMinglers(userId)
    }

    fun getMinglersByMingle(mingleId: Long): List<MinglerDto> {
        return minglerService.getMinglersByMingle(mingleId)
    }

    fun getMingler(userId: Long, minglerId: Long): MinglerDto {
        return minglerService.getMingler(userId, minglerId)
    }

    fun createMingler(userId: Long, request: CreateMinglerRequest): MinglerDto {
        return minglerService.createMingler(userId, request)
    }

    fun joinMingle(userId: Long, mingleId: Long): MinglerDto {
        return minglerService.joinMingle(userId, mingleId)
    }

    fun updateMingler(userId: Long, minglerId: Long, request: UpdateMinglerRequest): MinglerDto {
        return minglerService.updateMingler(userId, minglerId, request)
    }

    fun deleteMingler(userId: Long, minglerId: Long): Long {
        return minglerService.deleteMingler(userId, minglerId)
    }

    fun leaveMingle(userId: Long, mingleId: Long): Long {
        return minglerService.leaveMingle(userId, mingleId)
    }
}
