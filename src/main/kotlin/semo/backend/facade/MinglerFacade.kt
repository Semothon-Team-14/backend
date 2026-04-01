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

    fun getMingler(userId: Long, minglerId: Long): MinglerDto {
        return minglerService.getMingler(userId, minglerId)
    }

    fun createMingler(userId: Long, request: CreateMinglerRequest): MinglerDto {
        return minglerService.createMingler(userId, request)
    }

    fun updateMingler(userId: Long, minglerId: Long, request: UpdateMinglerRequest): MinglerDto {
        return minglerService.updateMingler(userId, minglerId, request)
    }

    fun deleteMingler(userId: Long, minglerId: Long): Long {
        return minglerService.deleteMingler(userId, minglerId)
    }
}
