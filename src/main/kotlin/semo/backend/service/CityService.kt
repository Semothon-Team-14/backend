package semo.backend.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import semo.backend.controller.request.CreateCityRequest
import semo.backend.controller.request.UpdateCityRequest
import semo.backend.dto.CityDto
import semo.backend.dto.NationalityCitiesDto
import semo.backend.entity.City
import semo.backend.entity.Nationality
import semo.backend.exception.city.CityNotFoundException
import semo.backend.exception.city.DuplicateCityNameEnglishException
import semo.backend.exception.nationality.NationalityNotFoundException
import semo.backend.mapstruct.CityMapStruct
import semo.backend.mapstruct.NationalityMapStruct
import semo.backend.repository.jpa.CityRepository
import semo.backend.repository.jpa.NationalityRepository
import semo.backend.util.applyIfProvided

@Service
@Transactional(readOnly = true)
class CityService(
    private val cityRepository: CityRepository,
    private val nationalityRepository: NationalityRepository,
    private val cityMapStruct: CityMapStruct,
    private val nationalityMapStruct: NationalityMapStruct,
) {
    fun getCities(nationalityId: Long): List<CityDto> {
        findNationalityById(nationalityId)
        return cityMapStruct.toDtos(cityRepository.findAllByNationalityIdOrderByCityNameEnglishAsc(nationalityId))
    }

    fun getCity(nationalityId: Long, cityId: Long): CityDto {
        return cityMapStruct.toDto(findCityByNationalityIdAndCityId(nationalityId, cityId))
    }

    fun getCitiesByNationality(): List<NationalityCitiesDto> {
        val nationalities = nationalityRepository.findAllByOrderByCountryNameEnglishAsc()
        return nationalities.map { nationality ->
            NationalityCitiesDto(
                nationality = nationalityMapStruct.toDto(nationality),
                cities = cityMapStruct.toDtos(
                    cityRepository.findAllByNationalityIdOrderByCityNameEnglishAsc(nationality.id),
                ),
            )
        }
    }

    @Transactional
    fun createCity(nationalityId: Long, request: CreateCityRequest): CityDto {
        val nationality = findNationalityById(nationalityId)
        val cityNameEnglish = normalizeCityName(request.cityNameEnglish)
        ensureCityNameEnglishAvailable(nationalityId, cityNameEnglish)
        val city = City(
            cityNameEnglish = cityNameEnglish,
            cityNameKorean = request.cityNameKorean.trim(),
            representativeImageUrl = normalizeUrlOrNull(request.representativeImageUrl),
            nationality = nationality,
        )
        return cityMapStruct.toDto(cityRepository.save(city))
    }

    @Transactional
    fun updateCity(nationalityId: Long, cityId: Long, request: UpdateCityRequest): CityDto {
        val city = findCityByNationalityIdAndCityId(nationalityId, cityId)
        request.cityNameEnglish.applyIfProvided { cityNameEnglish ->
            val normalizedCityNameEnglish = cityNameEnglish?.let(::normalizeCityName)
            ensureCityNameEnglishAvailable(nationalityId, normalizedCityNameEnglish, cityId)
            city.cityNameEnglish = normalizedCityNameEnglish ?: city.cityNameEnglish
        }
        request.cityNameKorean.applyIfProvided { cityNameKorean ->
            city.cityNameKorean = cityNameKorean?.trim() ?: city.cityNameKorean
        }
        request.representativeImageUrl.applyIfProvided { representativeImageUrl ->
            city.representativeImageUrl = normalizeUrlOrNull(representativeImageUrl)
        }
        return cityMapStruct.toDto(cityRepository.save(city))
    }

    @Transactional
    fun deleteCity(nationalityId: Long, cityId: Long): Long {
        val city = findCityByNationalityIdAndCityId(nationalityId, cityId)
        cityRepository.delete(city)
        return cityId
    }

    private fun findNationalityById(nationalityId: Long): Nationality {
        return nationalityRepository.findById(nationalityId)
            .orElseThrow { NationalityNotFoundException(nationalityId) }
    }

    private fun findCityByNationalityIdAndCityId(nationalityId: Long, cityId: Long): City {
        findNationalityById(nationalityId)
        return cityRepository.findByIdAndNationalityId(cityId, nationalityId)
            ?: throw CityNotFoundException(cityId)
    }

    private fun ensureCityNameEnglishAvailable(
        nationalityId: Long,
        cityNameEnglish: String?,
        cityId: Long? = null,
    ) {
        if (cityNameEnglish == null) {
            return
        }

        val exists = if (cityId == null) {
            cityRepository.existsByNationalityIdAndCityNameEnglish(nationalityId, cityNameEnglish)
        } else {
            cityRepository.existsByNationalityIdAndCityNameEnglishAndIdNot(nationalityId, cityNameEnglish, cityId)
        }

        if (exists) {
            throw DuplicateCityNameEnglishException(nationalityId, cityNameEnglish)
        }
    }

    private fun normalizeCityName(cityName: String): String {
        return cityName.trim()
    }

    private fun normalizeUrlOrNull(raw: String?): String? {
        val trimmed = raw?.trim()
        return if (trimmed.isNullOrEmpty()) {
            null
        } else {
            trimmed
        }
    }
}
