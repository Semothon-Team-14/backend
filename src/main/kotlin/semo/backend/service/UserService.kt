package semo.backend.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import semo.backend.controller.request.CreateUserRequest
import semo.backend.controller.request.UpdateUserRequest
import semo.backend.entity.Keyword
import semo.backend.entity.Nationality
import semo.backend.dto.UserDto
import semo.backend.entity.User
import semo.backend.exception.keyword.KeywordNotFoundException
import semo.backend.exception.nationality.NationalityNotFoundException
import semo.backend.exception.user.UserNotFoundException
import semo.backend.mapstruct.UserMapStruct
import semo.backend.repository.jpa.KeywordRepository
import semo.backend.repository.jpa.NationalityRepository
import semo.backend.repository.jpa.UserRepository
import java.util.Optional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val nationalityRepository: NationalityRepository,
    private val keywordRepository: KeywordRepository,
    private val userMapStruct: UserMapStruct,
) {
    fun getUsers(): List<UserDto> {
        return userMapStruct.toDtos(userRepository.findAll())
    }

    fun getUser(userId: Long): UserDto {
        return userMapStruct.toDto(findUserById(userId))
    }

    @Transactional
    fun createUser(request: CreateUserRequest): UserDto {
        val user = userMapStruct.toEntity(request)
        user.nationality = request.nationalityId?.let(::findNationalityById)
        user.keywords = resolveKeywords(request.keywordIds)
        val savedUser = userRepository.save(user)
        return userMapStruct.toDto(savedUser)
    }

    @Transactional
    fun updateUser(userId: Long, request: UpdateUserRequest): UserDto {
        val user = findUserById(userId)
        request.username.applyIfProvided { user.username = it }
        request.password.applyIfProvided { user.password = it }
        request.name.applyIfProvided { user.name = it }
        request.email.applyIfProvided { user.email = it }
        request.phone.applyIfProvided { user.phone = it }
        request.introduction.applyIfProvided { user.introduction = it }
        request.nationalityId.applyIfProvided { nationalityId ->
            user.nationality = nationalityId?.let(::findNationalityById)
        }
        request.keywordIds.applyIfProvided { keywordIds ->
            user.keywords = keywordIds?.let(::resolveKeywords) ?: mutableSetOf()
        }
        return userMapStruct.toDto(userRepository.save(user))
    }

    @Transactional
    fun deleteUser(userId: Long): Long {
        val user = findUserById(userId)
        userRepository.delete(user)
        return userId
    }

    private fun findUserById(userId: Long): User {
        return userRepository.findById(userId)
            .orElseThrow { UserNotFoundException(userId) }
    }

    private fun findNationalityById(nationalityId: Long): Nationality {
        return nationalityRepository.findById(nationalityId)
            .orElseThrow { NationalityNotFoundException(nationalityId) }
    }

    private fun resolveKeywords(keywordIds: List<Long>): MutableSet<Keyword> {
        if (keywordIds.isEmpty()) {
            return mutableSetOf()
        }

        val keywords = keywordRepository.findAllById(keywordIds)
        val foundKeywordIds = keywords.map { it.id }.toSet()
        val missingKeywordIds = keywordIds.filterNot(foundKeywordIds::contains)
        if (missingKeywordIds.isNotEmpty()) {
            throw KeywordNotFoundException(missingKeywordIds)
        }

        return keywords.toMutableSet()
    }

    private inline fun <T> Optional<T>?.applyIfProvided(
        block: (T?) -> Unit,
    ) {
        if (this != null) {
            block(orElse(null))
        }
    }
}
