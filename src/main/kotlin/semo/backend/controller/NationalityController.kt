package semo.backend.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import semo.backend.config.argument.OptionalRequestBody
import semo.backend.controller.request.CreateNationalityRequest
import semo.backend.controller.request.UpdateNationalityRequest
import semo.backend.controller.response.CreateNationalityResponse
import semo.backend.controller.response.DeleteNationalityResponse
import semo.backend.controller.response.GetNationalitiesResponse
import semo.backend.controller.response.GetNationalityResponse
import semo.backend.controller.response.UpdateNationalityResponse
import semo.backend.facade.NationalityFacade

@RestController
@RequestMapping("/nationalities")
class NationalityController(
    private val nationalityFacade: NationalityFacade,
) {
    @GetMapping
    fun getNationalities(): GetNationalitiesResponse {
        return GetNationalitiesResponse(
            nationalities = nationalityFacade.getNationalities(),
        )
    }

    @GetMapping("/{nationalityId}")
    fun getNationality(
        @PathVariable nationalityId: Long,
    ): GetNationalityResponse {
        return GetNationalityResponse(
            nationality = nationalityFacade.getNationality(nationalityId),
        )
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createNationality(
        @RequestBody request: CreateNationalityRequest,
    ): CreateNationalityResponse {
        return CreateNationalityResponse(
            nationality = nationalityFacade.createNationality(request),
        )
    }

    @PutMapping("/{nationalityId}")
    fun updateNationality(
        @PathVariable nationalityId: Long,
        @OptionalRequestBody request: UpdateNationalityRequest,
    ): UpdateNationalityResponse {
        return UpdateNationalityResponse(
            nationality = nationalityFacade.updateNationality(nationalityId, request),
        )
    }

    @DeleteMapping("/{nationalityId}")
    fun deleteNationality(
        @PathVariable nationalityId: Long,
    ): DeleteNationalityResponse {
        return DeleteNationalityResponse(
            deletedNationalityId = nationalityFacade.deleteNationality(nationalityId),
        )
    }
}
