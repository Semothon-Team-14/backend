package semo.backend.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import semo.backend.controller.request.CreateSavedCafeRequest
import semo.backend.controller.request.UpdateSavedCafeRequest
import semo.backend.dto.SavedCafeDto
import semo.backend.entity.Cafe
import semo.backend.entity.SavedCafe
import semo.backend.entity.User
import semo.backend.exception.cafe.CafeNotFoundException
import semo.backend.exception.savedcafe.SavedCafeDuplicateException
import semo.backend.exception.savedcafe.SavedCafeNotFoundException
import semo.backend.exception.savedcafe.SavedCafeTargetRequiredException
import semo.backend.exception.user.UserNotFoundException
import semo.backend.mapstruct.SavedCafeMapStruct
import semo.backend.repository.jpa.CafeRepository
import semo.backend.repository.jpa.SavedCafeRepository
import semo.backend.repository.jpa.UserRepository
import semo.backend.util.applyIfProvided

@Service
@Transactional(readOnly = true)
class SavedCafeService(
    private val savedCafeRepository: SavedCafeRepository,
    private val userRepository: UserRepository,
    private val cafeRepository: CafeRepository,
    private val savedCafeMapStruct: SavedCafeMapStruct,
) {
    fun getSavedCafes(userId: Long): List<SavedCafeDto> {
        findUserById(userId)
        return savedCafeMapStruct.toDtos(savedCafeRepository.findAllByUserIdOrderByCreatedDateTimeDesc(userId))
    }

    fun getSavedCafe(userId: Long, savedCafeId: Long): SavedCafeDto {
        return savedCafeMapStruct.toDto(findSavedCafe(userId, savedCafeId))
    }

    @Transactional
    fun createSavedCafe(userId: Long, request: CreateSavedCafeRequest): SavedCafeDto {
        val user = findUserById(userId)
        val cafe = findCafeById(request.cafeId)
        if (savedCafeRepository.existsByUserIdAndCafeId(userId, cafe.id)) {
            throw SavedCafeDuplicateException(userId, cafe.id)
        }
        val savedCafe = SavedCafe(
            user = user,
            cafe = cafe,
        )
        return savedCafeMapStruct.toDto(savedCafeRepository.save(savedCafe))
    }

    @Transactional
    fun updateSavedCafe(userId: Long, savedCafeId: Long, request: UpdateSavedCafeRequest): SavedCafeDto {
        val savedCafe = findSavedCafe(userId, savedCafeId)
        request.cafeId.applyIfProvided { cafeId ->
            val nextCafeId = cafeId ?: throw SavedCafeTargetRequiredException()
            if (savedCafeRepository.existsByUserIdAndCafeIdAndIdNot(userId, nextCafeId, savedCafeId)) {
                throw SavedCafeDuplicateException(userId, nextCafeId)
            }
            savedCafe.cafe = findCafeById(nextCafeId)
        }
        return savedCafeMapStruct.toDto(savedCafeRepository.save(savedCafe))
    }

    @Transactional
    fun deleteSavedCafe(userId: Long, savedCafeId: Long): Long {
        val savedCafe = findSavedCafe(userId, savedCafeId)
        savedCafeRepository.delete(savedCafe)
        return savedCafeId
    }

    @Transactional
    fun deleteAllByUserId(userId: Long) {
        savedCafeRepository.deleteAllByUserId(userId)
    }

    @Transactional
    fun deleteAllByCafeId(cafeId: Long) {
        savedCafeRepository.deleteAllByCafeId(cafeId)
    }

    private fun findSavedCafe(userId: Long, savedCafeId: Long): SavedCafe {
        findUserById(userId)
        return savedCafeRepository.findByIdAndUserId(savedCafeId, userId)
            ?: throw SavedCafeNotFoundException(savedCafeId)
    }

    private fun findUserById(userId: Long): User {
        return userRepository.findById(userId)
            .orElseThrow { UserNotFoundException(userId) }
    }

    private fun findCafeById(cafeId: Long): Cafe {
        return cafeRepository.findById(cafeId)
            .orElseThrow { CafeNotFoundException(cafeId) }
    }
}
