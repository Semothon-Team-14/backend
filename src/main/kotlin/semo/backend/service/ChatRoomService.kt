package semo.backend.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import semo.backend.controller.request.InitializeChatRoomRequest
import semo.backend.dto.ChatRoomDto
import semo.backend.entity.ChatParticipant
import semo.backend.entity.ChatRoom
import semo.backend.entity.User
import semo.backend.exception.chat.ChatRoomAccessDeniedException
import semo.backend.exception.chat.ChatRoomNotFoundException
import semo.backend.exception.chat.InvalidChatRoomInitializationException
import semo.backend.exception.chat.MingleChatRoomNotFoundException
import semo.backend.exception.user.UserNotFoundException
import semo.backend.mapstruct.ChatRoomMapStruct
import semo.backend.repository.jpa.ChatMessageRepository
import semo.backend.repository.jpa.ChatParticipantRepository
import semo.backend.repository.jpa.ChatRoomRepository
import semo.backend.repository.jpa.LocalRepository
import semo.backend.repository.jpa.TripRepository
import semo.backend.repository.jpa.UserRepository
import java.time.LocalDate
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
class ChatRoomService(
    private val chatRoomRepository: ChatRoomRepository,
    private val chatMessageRepository: ChatMessageRepository,
    private val chatParticipantRepository: ChatParticipantRepository,
    private val userRepository: UserRepository,
    private val mingleService: MingleService,
    private val minglerService: MinglerService,
    private val chatRoomMapStruct: ChatRoomMapStruct,
    private val localRepository: LocalRepository,
    private val tripRepository: TripRepository,
) {
    fun getChatRooms(userId: Long): List<ChatRoomDto> {
        findUserById(userId)
        val chatRooms = chatRoomRepository.findDistinctAllByParticipantsUserIdOrderByUpdatedDateTimeDesc(userId)
        val unreadCountByChatRoomId = findUnreadCountByChatRoomIdsAndUserId(
            chatRoomIds = chatRooms.map { it.id },
            userId = userId,
        )

        return chatRooms.map { chatRoom ->
            toDtoForUser(
                chatRoom = chatRoom,
                currentUserId = userId,
                unreadMessageCount = unreadCountByChatRoomId[chatRoom.id] ?: 0,
            )
        }
    }

    fun getChatRoom(userId: Long, chatRoomId: Long): ChatRoomDto {
        val chatRoom = findChatRoomForUser(userId, chatRoomId)
        val unreadCountByChatRoomId = findUnreadCountByChatRoomIdsAndUserId(
            chatRoomIds = listOf(chatRoomId),
            userId = userId,
        )
        return toDtoForUser(
            chatRoom = chatRoom,
            currentUserId = userId,
            unreadMessageCount = unreadCountByChatRoomId[chatRoomId] ?: 0,
        )
    }

    @Transactional
    fun initializeChatRoom(userId: Long, request: InitializeChatRoomRequest): ChatRoomDto {
        val currentUser = findUserById(userId)
        val mingle = request.mingleId?.let(mingleService::findMingleById)
        if (mingle != null) {
            val existingMingleChatRoom = chatRoomRepository.findByMingleId(mingle.id)
            if (existingMingleChatRoom != null) {
                return joinMingleChatRoom(userId, mingle.id)
            }
        }
        val otherParticipantIds = request.participantUserIds
            .map { it }
            .toSet()
            .filter { it != userId }

        if (otherParticipantIds.isEmpty()) {
            throw InvalidChatRoomInitializationException()
        }

        if (request.mingleId == null && otherParticipantIds.size == 1) {
            val otherUserId = otherParticipantIds.first()
            val existingDirectChatRoom = chatRoomRepository.findDirectChatRoomBetweenUsers(userId, otherUserId)
                ?: chatRoomRepository.findDirectChatRoomBetweenUsers(otherUserId, userId)
            if (existingDirectChatRoom != null) {
                return toDtoForUser(
                    chatRoom = existingDirectChatRoom,
                    currentUserId = userId,
                )
            }
        }

        val now = LocalDateTime.now()
        val chatRoom = chatRoomRepository.save(
            ChatRoom(
                name = request.name?.trim()?.takeIf { it.isNotEmpty() },
                directChat = request.mingleId == null && otherParticipantIds.size == 1,
                createdDateTime = now,
                updatedDateTime = now,
                mingle = mingle,
            ),
        )

        val participants = buildList {
            add(
                        ChatParticipant(
                            chatRoom = chatRoom,
                            user = currentUser,
                            joinedDateTime = now,
                            lastReadDateTime = now,
                        ),
                    )
            otherParticipantIds
                .map(::findUserById)
                .forEach { user ->
                    add(
                        ChatParticipant(
                            chatRoom = chatRoom,
                            user = user,
                            joinedDateTime = now,
                            lastReadDateTime = now,
                        ),
                    )
                }
        }

        chatParticipantRepository.saveAll(participants)
        chatRoom.participants = participants.toMutableSet()

        return toDtoForUser(
            chatRoom = chatRoom,
            currentUserId = userId,
        )
    }

    @Transactional
    fun createMingleChatRoom(mingleId: Long, participantUserIds: Set<Long>): ChatRoomDto {
        val existingChatRoom = chatRoomRepository.findByMingleId(mingleId)
        if (existingChatRoom != null) {
            return toDtoForUser(
                chatRoom = existingChatRoom,
                currentUserId = participantUserIds.firstOrNull() ?: 0,
            )
        }

        val mingle = mingleService.findMingleById(mingleId)
        val now = LocalDateTime.now()
        val chatRoom = chatRoomRepository.save(
            ChatRoom(
                name = mingle.title,
                directChat = false,
                createdDateTime = now,
                updatedDateTime = now,
                mingle = mingle,
            ),
        )

        val participants = participantUserIds
            .map(::findUserById)
            .distinctBy { it.id }
            .map { user ->
                ChatParticipant(
                    chatRoom = chatRoom,
                    user = user,
                    joinedDateTime = now,
                    lastReadDateTime = now,
                )
            }

        chatParticipantRepository.saveAll(participants)
        chatRoom.participants = participants.toMutableSet()
        return toDtoForUser(
            chatRoom = chatRoom,
            currentUserId = participantUserIds.firstOrNull() ?: 0,
        )
    }

    @Transactional
    fun joinMingleChatRoom(userId: Long, mingleId: Long): ChatRoomDto {
        minglerService.validateMingler(userId, mingleId)
        val chatRoom = chatRoomRepository.findByMingleId(mingleId)
            ?: throw MingleChatRoomNotFoundException(mingleId)

        val existingParticipant = chatParticipantRepository.findByChatRoomIdAndUserId(chatRoom.id, userId)
        if (existingParticipant == null) {
            chatRoom.updatedDateTime = LocalDateTime.now()
            chatParticipantRepository.save(
                ChatParticipant(
                    chatRoom = chatRoom,
                    user = findUserById(userId),
                    joinedDateTime = LocalDateTime.now(),
                    lastReadDateTime = LocalDateTime.now(),
                ),
            )
        }

        val reloaded = chatRoomRepository.findByIdAndParticipantsUserId(chatRoom.id, userId) ?: chatRoom
        return toDtoForUser(
            chatRoom = reloaded,
            currentUserId = userId,
        )
    }

    @Transactional
    fun ensureMingleChatRoomAndJoin(userId: Long, mingleId: Long): ChatRoomDto {
        minglerService.validateMingler(userId, mingleId)
        val existing = chatRoomRepository.findByMingleId(mingleId)
        if (existing == null) {
            val mingleParticipantUserIds = minglerService
                .getMinglersByMingle(mingleId)
                .map { it.userId }
                .toMutableSet()
            mingleParticipantUserIds.add(userId)
            createMingleChatRoom(mingleId, mingleParticipantUserIds)
        }
        return joinMingleChatRoom(userId, mingleId)
    }

    @Transactional
    fun leaveMingleChatRoom(userId: Long, mingleId: Long) {
        val chatRoom = chatRoomRepository.findByMingleId(mingleId) ?: return
        val participant = chatParticipantRepository.findByChatRoomIdAndUserId(chatRoom.id, userId) ?: return
        chatParticipantRepository.delete(participant)
        chatRoom.updatedDateTime = LocalDateTime.now()
        chatRoomRepository.save(chatRoom)
    }

    fun findChatRoomForUser(userId: Long, chatRoomId: Long): ChatRoom {
        val chatRoom = chatRoomRepository.findById(chatRoomId)
            .orElseThrow { ChatRoomNotFoundException(chatRoomId) }
        if (!chatParticipantRepository.existsByChatRoomIdAndUserId(chatRoomId, userId)) {
            throw ChatRoomAccessDeniedException(chatRoomId, userId)
        }
        return chatRoomRepository.findByIdAndParticipantsUserId(chatRoomId, userId)
            ?: chatRoom
    }

    fun validateParticipation(userId: Long, chatRoomId: Long) {
        if (!chatParticipantRepository.existsByChatRoomIdAndUserId(chatRoomId, userId)) {
            throw ChatRoomAccessDeniedException(chatRoomId, userId)
        }
    }

    @Transactional
    fun markChatRoomAsRead(userId: Long, chatRoomId: Long): LocalDateTime {
        val participant = chatParticipantRepository.findByChatRoomIdAndUserId(chatRoomId, userId)
            ?: throw ChatRoomAccessDeniedException(chatRoomId, userId)
        val now = LocalDateTime.now()
        participant.lastReadDateTime = now
        return now
    }

    private fun findUnreadCountByChatRoomIdsAndUserId(chatRoomIds: Collection<Long>, userId: Long): Map<Long, Long> {
        if (chatRoomIds.isEmpty()) {
            return emptyMap()
        }

        return chatMessageRepository.countUnreadMessagesByChatRoomIdsAndUserId(chatRoomIds, userId)
            .associate { projection ->
                projection.chatRoomId to projection.unreadCount
            }
    }

    private fun findUserById(userId: Long): User {
        return userRepository.findById(userId)
            .orElseThrow { UserNotFoundException(userId) }
    }

    private fun toDtoForUser(
        chatRoom: ChatRoom,
        currentUserId: Long,
        unreadMessageCount: Long = 0,
    ): ChatRoomDto {
        val base = chatRoomMapStruct.toDto(chatRoom)
        val resolvedMingleName = if (base.mingleId != null) {
            val currentName = base.name.orEmpty().trim()
            val shouldUseMingleTitle =
                currentName.isBlank() || currentName.startsWith("Mingle #")
            if (shouldUseMingleTitle) {
                chatRoom.mingle?.title?.trim()?.ifEmpty { null } ?: base.name
            } else {
                base.name
            }
        } else {
            base.name
        }
        val otherParticipantId = if (base.participantUserIds.size == 2) {
            base.participantUserIds.firstOrNull { it != currentUserId }
        } else {
            null
        }
        val otherParticipantLocal = otherParticipantId?.let { participantId ->
            val travelerNow = tripRepository.existsActiveTripByUserId(
                userId = participantId,
                targetDate = LocalDate.now(),
            )
            if (travelerNow) {
                false
            } else {
                localRepository.existsByUserId(participantId)
            }
        }

        return base.copy(
            name = resolvedMingleName,
            unreadMessageCount = unreadMessageCount,
            otherParticipantLocal = otherParticipantLocal,
        )
    }
}
