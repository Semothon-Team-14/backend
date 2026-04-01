package semo.backend.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import semo.backend.controller.request.CreateLocalRequest
import semo.backend.controller.request.UpdateLocalRequest
import semo.backend.dto.LocalDto
import semo.backend.entity.City
import semo.backend.entity.Local
import semo.backend.entity.User
import semo.backend.exception.city.CityNotFoundException
import semo.backend.exception.local.LocalCityRequiredException
import semo.backend.exception.local.LocalDuplicateException
import semo.backend.exception.local.LocalNotFoundException
import semo.backend.exception.user.UserNotFoundException
import semo.backend.mapstruct.LocalMapStruct
import semo.backend.repository.jpa.CityRepository
import semo.backend.repository.jpa.LocalRepository
import semo.backend.repository.jpa.UserRepository
import semo.backend.util.applyIfProvided

@Service
class LocalService(
    private val localRepository: LocalRepository,
    private val userRepository: UserRepository,
    private val cityRepository: CityRepository,
    private val localMapStruct: LocalMapStruct,
) {
    fun getLocals(userId: Long): List<LocalDto> {
        findUserById(userId)
        return localMapStruct.toDtos(localRepository.findAllByUserIdOrderByCreatedDateTimeDesc(userId))
    }

    fun getLocal(userId: Long, localId: Long): LocalDto {
        return localMapStruct.toDto(findLocal(userId, localId))
    }

    @Transactional
    fun createLocal(userId: Long, request: CreateLocalRequest): LocalDto {
        val user = findUserById(userId)
        val city = findCityById(request.cityId)
        if (localRepository.existsByUserIdAndCityId(userId, city.id)) {
            throw LocalDuplicateException(userId, city.id)
        }
        return localMapStruct.toDto(
            localRepository.save(
                Local(
                    user = user,
                    city = city,
                ),
            ),
        )
    }

    @Transactional
    fun updateLocal(userId: Long, localId: Long, request: UpdateLocalRequest): LocalDto {
        val local = findLocal(userId, localId)
        request.cityId.applyIfProvided { cityId ->
            val nextCityId = cityId ?: throw LocalCityRequiredException()
            if (localRepository.existsByUserIdAndCityIdAndIdNot(userId, nextCityId, localId)) {
                throw LocalDuplicateException(userId, nextCityId)
            }
            local.city = findCityById(nextCityId)
        }
        return localMapStruct.toDto(localRepository.save(local))
    }

    @Transactional
    fun deleteLocal(userId: Long, localId: Long): Long {
        val local = findLocal(userId, localId)
        localRepository.delete(local)
        return localId
    }

    @Transactional
    fun deleteAllByUserId(userId: Long) {
        localRepository.deleteAllByUserId(userId)
    }

    fun findLocalUserIdsByCityIdExcludingUserId(cityId: Long, excludeUserId: Long): List<Long> {
        return localRepository.findDistinctUserIdsByCityIdExcludingUserId(cityId, excludeUserId)
    }

    private fun findLocal(userId: Long, localId: Long): Local {
        findUserById(userId)
        return localRepository.findByIdAndUserId(localId, userId)
            ?: throw LocalNotFoundException(localId)
    }

    private fun findUserById(userId: Long): User {
        return userRepository.findById(userId)
            .orElseThrow { UserNotFoundException(userId) }
    }

    private fun findCityById(cityId: Long): City {
        return cityRepository.findById(cityId)
            .orElseThrow { CityNotFoundException(cityId) }
    }
}
