package semo.backend.facade

import org.springframework.stereotype.Service
import semo.backend.controller.request.CreateLocalRequest
import semo.backend.controller.request.UpdateLocalRequest
import semo.backend.dto.LocalDto
import semo.backend.service.LocalService

@Service
class LocalFacade(
    private val localService: LocalService,
) {
    fun getLocals(userId: Long): List<LocalDto> {
        return localService.getLocals(userId)
    }

    fun getLocal(userId: Long, localId: Long): LocalDto {
        return localService.getLocal(userId, localId)
    }

    fun createLocal(userId: Long, request: CreateLocalRequest): LocalDto {
        return localService.createLocal(userId, request)
    }

    fun updateLocal(userId: Long, localId: Long, request: UpdateLocalRequest): LocalDto {
        return localService.updateLocal(userId, localId, request)
    }

    fun deleteLocal(userId: Long, localId: Long): Long {
        return localService.deleteLocal(userId, localId)
    }
}
