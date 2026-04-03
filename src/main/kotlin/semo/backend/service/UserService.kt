package semo.backend.service

import jakarta.persistence.EntityManager
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import semo.backend.config.AwsS3Properties
import semo.backend.controller.request.CreateUserRequest
import semo.backend.controller.request.UpdateUserRequest
import semo.backend.entity.Nationality
import semo.backend.dto.UserDto
import semo.backend.entity.Keyword
import semo.backend.entity.User
import semo.backend.exception.keyword.KeywordNotFoundException
import semo.backend.exception.nationality.NationalityNotFoundException
import semo.backend.exception.user.InvalidProfileImageException
import semo.backend.exception.user.ProfileImageStorageNotConfiguredException
import semo.backend.exception.user.UserNotFoundException
import semo.backend.mapstruct.UserMapStruct
import semo.backend.repository.jpa.KeywordRepository
import semo.backend.repository.jpa.LocalRepository
import semo.backend.repository.jpa.MinglerRepository
import semo.backend.repository.jpa.NationalityRepository
import semo.backend.repository.jpa.QuickMatchRepository
import semo.backend.repository.jpa.QuickMatchResponseRepository
import semo.backend.repository.jpa.SavedCafeRepository
import semo.backend.repository.jpa.SavedRestaurantRepository
import semo.backend.repository.jpa.UserRepository
import semo.backend.util.applyIfProvided
import java.util.UUID
import kotlin.random.Random

@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
    private val nationalityRepository: NationalityRepository,
    private val keywordRepository: KeywordRepository,
    private val userMapStruct: UserMapStruct,
    private val entityManager: EntityManager,
    private val savedCafeRepository: SavedCafeRepository,
    private val savedRestaurantRepository: SavedRestaurantRepository,
    private val localRepository: LocalRepository,
    private val minglerRepository: MinglerRepository,
    private val quickMatchRepository: QuickMatchRepository,
    private val quickMatchResponseRepository: QuickMatchResponseRepository,
    private val awsS3Properties: AwsS3Properties,
    private val awsS3StorageService: AwsS3StorageService? = null,
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
        if (user.profileImageUrl.isNullOrBlank()) {
            user.profileImageUrl = pickRandomDefaultProfileImageUrl()
        }
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
        request.sex.applyIfProvided { user.sex = it }
        request.introduction.applyIfProvided { user.introduction = it }
        request.profileImageUrl.applyIfProvided { user.profileImageUrl = it }
        request.nationalityId.applyIfProvided { nationalityId ->
            user.nationality = nationalityId?.let(::findNationalityById)
        }
        request.keywordIds.applyIfProvided { keywordIds ->
            user.keywords = keywordIds?.let(::resolveKeywords) ?: mutableSetOf()
        }
        return userMapStruct.toDto(userRepository.save(user))
    }

    @Transactional
    fun uploadProfileImage(userId: Long, file: MultipartFile): UserDto {
        val storageService = awsS3StorageService ?: throw ProfileImageStorageNotConfiguredException()
        if (file.isEmpty || file.size <= 0L) {
            throw InvalidProfileImageException("Profile image file is empty")
        }

        val contentType = file.contentType?.trim().orEmpty()
        if (!contentType.startsWith("image/")) {
            throw InvalidProfileImageException("Profile image must be an image file")
        }

        val extension = resolveFileExtension(file.originalFilename, contentType)
        val key = "users/$userId/${UUID.randomUUID()}.$extension"
        val uploadedUrl = file.inputStream.use { inputStream ->
            storageService.uploadPublicObjectToBucket(
                bucket = awsS3Properties.profilePicturesBucket,
                key = key,
                contentType = contentType,
                contentLength = file.size,
                inputStream = inputStream,
            )
        }

        val user = findUserById(userId)
        user.profileImageUrl = uploadedUrl
        return userMapStruct.toDto(userRepository.save(user))
    }

    @Transactional
    fun deleteUser(userId: Long): Long {
        val user = findUserById(userId)
        savedCafeRepository.deleteAllByUserId(userId)
        savedRestaurantRepository.deleteAllByUserId(userId)
        localRepository.deleteAllByUserId(userId)
        minglerRepository.deleteAllByUserId(userId)
        quickMatchResponseRepository.deleteAllByResponderUserId(userId)
        quickMatchRepository.deleteAllByRequesterUserIdOrAcceptedByUserId(userId, userId)
        entityManager.createNativeQuery(
            """
            delete from chat_message_translations
            where user_id = :userId
               or chat_message_id in (
                   select id
                   from chat_messages
                   where sender_user_id = :userId
               )
            """.trimIndent(),
        )
            .setParameter("userId", userId)
            .executeUpdate()
        entityManager.createNativeQuery(
            """
            delete from chat_messages
            where sender_user_id = :userId
            """.trimIndent(),
        )
            .setParameter("userId", userId)
            .executeUpdate()
        entityManager.createNativeQuery(
            """
            delete from chat_participants
            where user_id = :userId
            """.trimIndent(),
        )
            .setParameter("userId", userId)
            .executeUpdate()
        entityManager.createNativeQuery(
            """
            delete from trips
            where user_id = :userId
            """.trimIndent(),
        )
            .setParameter("userId", userId)
            .executeUpdate()
        entityManager.createNativeQuery(
            """
            delete from user_keywords
            where user_id = :userId
            """.trimIndent(),
        )
            .setParameter("userId", userId)
            .executeUpdate()
        entityManager.createNativeQuery(
            """
            delete from chat_rooms
            where not exists (
                select 1
                from chat_participants
                where chat_participants.chat_room_id = chat_rooms.id
            )
            """.trimIndent(),
        )
            .executeUpdate()
        entityManager.flush()
        entityManager.refresh(user)
        userRepository.delete(user)
        return userId
    }

    fun findUserById(userId: Long): User {
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

    private fun resolveFileExtension(originalFilename: String?, contentType: String): String {
        val fromFilename = originalFilename
            ?.substringAfterLast('.', "")
            ?.trim()
            ?.lowercase()
            ?.takeIf { it.isNotEmpty() }
        if (fromFilename != null) {
            return fromFilename
        }

        return when (contentType.lowercase()) {
            "image/jpeg", "image/jpg" -> "jpg"
            "image/png" -> "png"
            "image/webp" -> "webp"
            "image/heic" -> "heic"
            else -> "bin"
        }
    }

    private fun pickRandomDefaultProfileImageUrl(): String {
        val avatarIndex = Random.nextInt(1, 10)
        val key = "defaults/profile-avatars/avatar_$avatarIndex.png"
        return awsS3Properties.buildPublicUrlForBucket(awsS3Properties.profilePicturesBucket, key)
    }
}
