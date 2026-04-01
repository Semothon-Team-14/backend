package semo.backend.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import semo.backend.controller.request.CreateMingleRequest
import semo.backend.controller.request.UpdateMingleRequest
import semo.backend.dto.MingleDto
import semo.backend.entity.City
import semo.backend.entity.Mingle
import semo.backend.exception.city.CityNotFoundException
import semo.backend.exception.mingle.MingleCityRequiredException
import semo.backend.exception.mingle.MingleNotFoundException
import semo.backend.exception.mingle.MingleTitleRequiredException
import semo.backend.mapstruct.MingleMapStruct
import semo.backend.repository.jpa.CityRepository
import semo.backend.repository.jpa.MingleRepository
import semo.backend.repository.jpa.MinglerRepository
import semo.backend.util.applyIfProvided

@Service
class MingleService(
    private val mingleRepository: MingleRepository,
    private val minglerRepository: MinglerRepository,
    private val cityRepository: CityRepository,
    private val mingleMapStruct: MingleMapStruct,
) {
    fun getMingles(cityId: Long?): List<MingleDto> {
        val mingles = if (cityId == null) {
            mingleRepository.findAll()
        } else {
            findCityById(cityId)
            mingleRepository.findAllByCityIdOrderByCreatedDateTimeDesc(cityId)
        }
        return mingleMapStruct.toDtos(mingles)
    }

    fun getMingle(mingleId: Long): MingleDto {
        return mingleMapStruct.toDto(findMingleById(mingleId))
    }

    @Transactional
    fun createMingle(request: CreateMingleRequest): MingleDto {
        val mingle = Mingle(
            city = findCityById(request.cityId),
            title = request.title,
            description = request.description,
        )
        return mingleMapStruct.toDto(mingleRepository.save(mingle))
    }

    @Transactional
    fun updateMingle(mingleId: Long, request: UpdateMingleRequest): MingleDto {
        val mingle = findMingleById(mingleId)
        request.cityId.applyIfProvided { cityId ->
            val nextCityId = cityId ?: throw MingleCityRequiredException()
            mingle.city = findCityById(nextCityId)
        }
        request.title.applyIfProvided { title ->
            mingle.title = title ?: throw MingleTitleRequiredException()
        }
        request.description.applyIfProvided { description -> mingle.description = description }
        return mingleMapStruct.toDto(mingleRepository.save(mingle))
    }

    @Transactional
    fun deleteMingle(mingleId: Long): Long {
        findMingleById(mingleId)
        minglerRepository.deleteAllByMingleId(mingleId)
        mingleRepository.deleteById(mingleId)
        return mingleId
    }

    fun findMingleById(mingleId: Long): Mingle {
        return mingleRepository.findById(mingleId)
            .orElseThrow { MingleNotFoundException(mingleId) }
    }

    private fun findCityById(cityId: Long): City {
        return cityRepository.findById(cityId)
            .orElseThrow { CityNotFoundException(cityId) }
    }
}
