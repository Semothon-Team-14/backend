package semo.backend.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import semo.backend.controller.request.CreateNationalityRequest
import semo.backend.controller.request.UpdateNationalityRequest
import semo.backend.dto.NationalityDto
import semo.backend.entity.Nationality
import semo.backend.exception.nationality.DuplicateNationalityCountryCodeException
import semo.backend.exception.nationality.NationalityNotFoundException
import semo.backend.mapstruct.NationalityMapStruct
import semo.backend.repository.jpa.NationalityRepository
import semo.backend.util.applyIfProvided

@Service
@Transactional(readOnly = true)
class NationalityService(
    private val nationalityRepository: NationalityRepository,
    private val nationalityMapStruct: NationalityMapStruct,
) {
    fun getNationalities(): List<NationalityDto> {
        return nationalityMapStruct.toDtos(nationalityRepository.findAllByOrderByCountryNameEnglishAsc())
    }

    fun searchNationalities(query: String): List<NationalityDto> {
        return nationalityMapStruct.toDtos(nationalityRepository.search(query.trim()))
    }

    fun getNationality(nationalityId: Long): NationalityDto {
        return nationalityMapStruct.toDto(findNationalityById(nationalityId))
    }

    @Transactional
    fun createNationality(request: CreateNationalityRequest): NationalityDto {
        val countryCode = normalizeCountryCode(request.countryCode)
        ensureCountryCodeAvailable(countryCode)
        val nationality = Nationality(
            countryCode = countryCode,
            countryNameEnglish = request.countryNameEnglish.trim(),
            countryNameKorean = request.countryNameKorean.trim(),
        )
        return nationalityMapStruct.toDto(nationalityRepository.save(nationality))
    }

    @Transactional
    fun updateNationality(nationalityId: Long, request: UpdateNationalityRequest): NationalityDto {
        val nationality = findNationalityById(nationalityId)
        request.countryCode.applyIfProvided { countryCode ->
            val normalizedCountryCode = countryCode?.let(::normalizeCountryCode)
            ensureCountryCodeAvailable(normalizedCountryCode, nationalityId)
            nationality.countryCode = normalizedCountryCode ?: nationality.countryCode
        }
        request.countryNameEnglish.applyIfProvided { countryNameEnglish ->
            nationality.countryNameEnglish = countryNameEnglish?.trim() ?: nationality.countryNameEnglish
        }
        request.countryNameKorean.applyIfProvided { countryNameKorean ->
            nationality.countryNameKorean = countryNameKorean?.trim() ?: nationality.countryNameKorean
        }
        return nationalityMapStruct.toDto(nationalityRepository.save(nationality))
    }

    @Transactional
    fun deleteNationality(nationalityId: Long): Long {
        val nationality = findNationalityById(nationalityId)
        nationalityRepository.delete(nationality)
        return nationalityId
    }

    private fun findNationalityById(nationalityId: Long): Nationality {
        return nationalityRepository.findById(nationalityId)
            .orElseThrow { NationalityNotFoundException(nationalityId) }
    }

    private fun ensureCountryCodeAvailable(
        countryCode: String?,
        nationalityId: Long? = null,
    ) {
        if (countryCode == null) {
            return
        }

        val exists = if (nationalityId == null) {
            nationalityRepository.existsByCountryCode(countryCode)
        } else {
            nationalityRepository.existsByCountryCodeAndIdNot(countryCode, nationalityId)
        }

        if (exists) {
            throw DuplicateNationalityCountryCodeException(countryCode)
        }
    }

    private fun normalizeCountryCode(countryCode: String): String {
        return countryCode.trim().uppercase()
    }
}
