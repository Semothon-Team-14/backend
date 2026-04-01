package semo.backend.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import semo.backend.controller.request.CreateMinglerRequest
import semo.backend.controller.request.UpdateMinglerRequest
import semo.backend.dto.MinglerDto
import semo.backend.entity.Mingler
import semo.backend.entity.User
import semo.backend.exception.mingler.MinglerDuplicateException
import semo.backend.exception.mingler.MinglerMembershipNotFoundException
import semo.backend.exception.mingler.MinglerMingleRequiredException
import semo.backend.exception.mingler.MinglerNotFoundException
import semo.backend.exception.user.UserNotFoundException
import semo.backend.mapstruct.MinglerMapStruct
import semo.backend.repository.jpa.MinglerRepository
import semo.backend.repository.jpa.UserRepository
import semo.backend.util.applyIfProvided

@Service
class MinglerService(
    private val minglerRepository: MinglerRepository,
    private val userRepository: UserRepository,
    private val mingleService: MingleService,
    private val minglerMapStruct: MinglerMapStruct,
) {
    fun getMinglers(userId: Long): List<MinglerDto> {
        findUserById(userId)
        return minglerMapStruct.toDtos(minglerRepository.findAllByUserIdOrderByCreatedDateTimeDesc(userId))
    }

    fun getMinglersByMingle(mingleId: Long): List<MinglerDto> {
        mingleService.findMingleById(mingleId)
        return minglerMapStruct.toDtos(minglerRepository.findAllByMingleIdOrderByCreatedDateTimeDesc(mingleId))
    }

    fun getMingler(userId: Long, minglerId: Long): MinglerDto {
        return minglerMapStruct.toDto(findMingler(userId, minglerId))
    }

    @Transactional
    fun createMingler(userId: Long, request: CreateMinglerRequest): MinglerDto {
        val user = findUserById(userId)
        val mingle = mingleService.findMingleById(request.mingleId)
        if (minglerRepository.existsByMingleIdAndUserId(mingle.id, userId)) {
            throw MinglerDuplicateException(userId, mingle.id)
        }
        val mingler = Mingler(
            mingle = mingle,
            user = user,
        )
        return minglerMapStruct.toDto(minglerRepository.save(mingler))
    }

    @Transactional
    fun joinMingle(userId: Long, mingleId: Long): MinglerDto {
        return createMingler(
            userId = userId,
            request = CreateMinglerRequest(mingleId = mingleId),
        )
    }

    @Transactional
    fun ensureJoinedMingle(userId: Long, mingleId: Long): Mingler {
        val existing = minglerRepository.findByMingleIdAndUserId(mingleId, userId)
        if (existing != null) {
            return existing
        }
        return minglerRepository.save(
            Mingler(
                mingle = mingleService.findMingleById(mingleId),
                user = findUserById(userId),
            ),
        )
    }

    @Transactional
    fun updateMingler(userId: Long, minglerId: Long, request: UpdateMinglerRequest): MinglerDto {
        val mingler = findMingler(userId, minglerId)
        request.mingleId.applyIfProvided { mingleId ->
            val nextMingleId = mingleId ?: throw MinglerMingleRequiredException()
            if (minglerRepository.existsByMingleIdAndUserIdAndIdNot(nextMingleId, userId, minglerId)) {
                throw MinglerDuplicateException(userId, nextMingleId)
            }
            mingler.mingle = mingleService.findMingleById(nextMingleId)
        }
        return minglerMapStruct.toDto(minglerRepository.save(mingler))
    }

    @Transactional
    fun deleteMingler(userId: Long, minglerId: Long): Long {
        val mingler = findMingler(userId, minglerId)
        minglerRepository.delete(mingler)
        return minglerId
    }

    @Transactional
    fun leaveMingle(userId: Long, mingleId: Long): Long {
        findUserById(userId)
        mingleService.findMingleById(mingleId)
        val mingler = minglerRepository.findByMingleIdAndUserId(mingleId, userId)
            ?: throw MinglerMembershipNotFoundException(userId, mingleId)
        val minglerId = mingler.id
        minglerRepository.delete(mingler)
        return minglerId
    }

    fun validateMingler(userId: Long, mingleId: Long): Mingler {
        findUserById(userId)
        mingleService.findMingleById(mingleId)
        return minglerRepository.findByMingleIdAndUserId(mingleId, userId)
            ?: throw MinglerMembershipNotFoundException(userId, mingleId)
    }

    @Transactional
    fun deleteAllByUserId(userId: Long) {
        minglerRepository.deleteAllByUserId(userId)
    }

    private fun findMingler(userId: Long, minglerId: Long): Mingler {
        findUserById(userId)
        return minglerRepository.findByIdAndUserId(minglerId, userId)
            ?: throw MinglerNotFoundException(minglerId)
    }

    private fun findUserById(userId: Long): User {
        return userRepository.findById(userId)
            .orElseThrow { UserNotFoundException(userId) }
    }
}
