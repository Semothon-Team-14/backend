package semo.backend.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import semo.backend.controller.request.CreateMingleRequest
import semo.backend.controller.request.UpdateMingleRequest
import semo.backend.dto.MingleDto
import semo.backend.entity.City
import semo.backend.entity.Mingle
import semo.backend.entity.Mingler
import semo.backend.entity.User
import semo.backend.exception.city.CityNotFoundException
import semo.backend.exception.mingle.MingleCityRequiredException
import semo.backend.exception.mingle.MingleNotFoundException
import semo.backend.exception.mingle.MingleTitleRequiredException
import semo.backend.exception.user.UserNotFoundException
import semo.backend.mapstruct.MingleMapStruct
import semo.backend.repository.jpa.CityRepository
import semo.backend.repository.jpa.MingleRepository
import semo.backend.repository.jpa.MinglerRepository
import semo.backend.repository.jpa.QuickMatchRepository
import semo.backend.repository.jpa.QuickMatchResponseRepository
import semo.backend.repository.jpa.UserRepository
import semo.backend.util.applyIfProvided
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
class MingleService(
    private val mingleRepository: MingleRepository,
    private val minglerRepository: MinglerRepository,
    private val quickMatchRepository: QuickMatchRepository,
    private val quickMatchResponseRepository: QuickMatchResponseRepository,
    private val cityRepository: CityRepository,
    private val userRepository: UserRepository,
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
    fun createMingle(userId: Long, request: CreateMingleRequest): MingleDto {
        val mingle = createMingleEntity(
            cityId = request.cityId,
            title = request.title,
            description = request.description,
            placeName = request.placeName,
            meetDateTime = request.meetDateTime,
            latitude = request.latitude,
            longitude = request.longitude,
            targetParticipantCount = request.targetParticipantCount,
        )
        val savedMingle = mingleRepository.save(mingle)
        minglerRepository.save(
            Mingler(
                mingle = savedMingle,
                user = findUserById(userId),
            ),
        )
        return mingleMapStruct.toDto(savedMingle)
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
        request.placeName.applyIfProvided { placeName -> mingle.placeName = placeName?.trim()?.takeIf { it.isNotEmpty() } }
        request.meetDateTime.applyIfProvided { meetDateTime -> mingle.meetDateTime = meetDateTime }
        request.latitude.applyIfProvided { latitude -> mingle.latitude = latitude }
        request.longitude.applyIfProvided { longitude -> mingle.longitude = longitude }
        request.targetParticipantCount.applyIfProvided { targetParticipantCount ->
            mingle.targetParticipantCount = normalizeTargetParticipantCount(targetParticipantCount)
        }
        return mingleMapStruct.toDto(mingleRepository.save(mingle))
    }

    @Transactional
    fun deleteMingle(mingleId: Long): Long {
        findMingleById(mingleId)
        minglerRepository.deleteAllByMingleId(mingleId)
        val quickMatches = quickMatchRepository.findAllByMingleId(mingleId)
        if (quickMatches.isNotEmpty()) {
            quickMatchResponseRepository.deleteAllByQuickMatchIdIn(quickMatches.map { it.id })
            quickMatchRepository.deleteAll(quickMatches)
        }
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

    private fun findUserById(userId: Long): User {
        return userRepository.findById(userId)
            .orElseThrow { UserNotFoundException(userId) }
    }

    private fun createMingleEntity(
        cityId: Long,
        title: String,
        description: String?,
        placeName: String?,
        meetDateTime: LocalDateTime?,
        latitude: BigDecimal?,
        longitude: BigDecimal?,
        targetParticipantCount: Int?,
    ): Mingle {
        return Mingle(
            city = findCityById(cityId),
            title = title,
            description = description,
            placeName = placeName?.trim()?.takeIf { it.isNotEmpty() },
            meetDateTime = meetDateTime,
            latitude = latitude,
            longitude = longitude,
            targetParticipantCount = normalizeTargetParticipantCount(targetParticipantCount),
        )
    }

    private fun normalizeTargetParticipantCount(raw: Int?): Int? {
        if (raw == null) {
            return null
        }

        return raw.coerceAtLeast(2)
    }
}
