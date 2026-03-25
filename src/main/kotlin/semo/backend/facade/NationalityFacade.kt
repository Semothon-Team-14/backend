package semo.backend.facade

import org.springframework.stereotype.Service
import semo.backend.controller.request.CreateNationalityRequest
import semo.backend.controller.request.UpdateNationalityRequest
import semo.backend.dto.NationalityDto
import semo.backend.service.NationalityService

@Service
class NationalityFacade(
    private val nationalityService: NationalityService,
) {
    fun getNationalities(): List<NationalityDto> {
        return nationalityService.getNationalities()
    }

    fun getNationality(nationalityId: Long): NationalityDto {
        return nationalityService.getNationality(nationalityId)
    }

    fun createNationality(request: CreateNationalityRequest): NationalityDto {
        return nationalityService.createNationality(request)
    }

    fun updateNationality(nationalityId: Long, request: UpdateNationalityRequest): NationalityDto {
        return nationalityService.updateNationality(nationalityId, request)
    }

    fun deleteNationality(nationalityId: Long): Long {
        return nationalityService.deleteNationality(nationalityId)
    }
}
