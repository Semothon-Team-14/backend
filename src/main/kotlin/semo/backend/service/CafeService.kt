package semo.backend.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import semo.backend.controller.request.CreateCafeRequest
import semo.backend.controller.request.UpdateCafeRequest
import semo.backend.dto.CafeDto
import semo.backend.entity.Cafe
import semo.backend.entity.City
import semo.backend.exception.cafe.CafeNotFoundException
import semo.backend.exception.city.CityNotFoundException
import semo.backend.mapstruct.CafeMapStruct
import semo.backend.repository.jpa.CafeImageRepository
import semo.backend.repository.jpa.CafeRepository
import semo.backend.repository.jpa.CityRepository
import semo.backend.repository.jpa.SavedCafeRepository
import semo.backend.util.applyIfProvided

@Service
class CafeService(
    private val cafeRepository: CafeRepository,
    private val cityRepository: CityRepository,
    private val cafeImageRepository: CafeImageRepository,
    private val cafeMapStruct: CafeMapStruct,
    private val savedCafeRepository: SavedCafeRepository,
) {
    fun getCafes(cityId: Long): List<CafeDto> {
        findCityById(cityId)
        return cafeMapStruct.toDtos(cafeRepository.findAllByCityIdOrderByNameAsc(cityId))
    }

    fun getCafe(cityId: Long, cafeId: Long): CafeDto {
        return cafeMapStruct.toDto(findCafeByCityIdAndCafeId(cityId, cafeId))
    }

    @Transactional
    fun createCafe(cityId: Long, request: CreateCafeRequest): CafeDto {
        val city = findCityById(cityId)
        val cafe = Cafe(
            name = request.name.trim(),
            phoneNumber = request.phoneNumber?.trim(),
            address = request.address?.trim(),
            foodCategory = request.foodCategory?.trim(),
            latitude = request.latitude,
            longitude = request.longitude,
            city = city,
        )
        return cafeMapStruct.toDto(cafeRepository.save(cafe))
    }

    @Transactional
    fun updateCafe(cityId: Long, cafeId: Long, request: UpdateCafeRequest): CafeDto {
        val cafe = findCafeByCityIdAndCafeId(cityId, cafeId)
        request.name.applyIfProvided { name -> cafe.name = name?.trim() ?: cafe.name }
        request.phoneNumber.applyIfProvided { phoneNumber -> cafe.phoneNumber = phoneNumber?.trim() }
        request.address.applyIfProvided { address -> cafe.address = address?.trim() }
        request.foodCategory.applyIfProvided { foodCategory -> cafe.foodCategory = foodCategory?.trim() }
        request.latitude.applyIfProvided { latitude -> cafe.latitude = latitude }
        request.longitude.applyIfProvided { longitude -> cafe.longitude = longitude }
        return cafeMapStruct.toDto(cafeRepository.save(cafe))
    }

    @Transactional
    fun deleteCafe(cityId: Long, cafeId: Long): Long {
        val cafe = findCafeByCityIdAndCafeId(cityId, cafeId)
        savedCafeRepository.deleteAllByCafeId(cafeId)
        val images = cafeImageRepository.findAllByCafeIdOrderByIdAsc(cafeId)
        if (images.isNotEmpty()) {
            cafeImageRepository.deleteAll(images)
        }
        cafeRepository.delete(cafe)
        return cafeId
    }

    fun findCafeById(cafeId: Long): Cafe {
        return cafeRepository.findById(cafeId)
            .orElseThrow { CafeNotFoundException(cafeId) }
    }

    private fun findCityById(cityId: Long): City {
        return cityRepository.findById(cityId)
            .orElseThrow { CityNotFoundException(cityId) }
    }

    private fun findCafeByCityIdAndCafeId(cityId: Long, cafeId: Long): Cafe {
        findCityById(cityId)
        return cafeRepository.findByIdAndCityId(cafeId, cityId)
            ?: throw CafeNotFoundException(cafeId)
    }
}
