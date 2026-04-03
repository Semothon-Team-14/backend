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
import semo.backend.repository.jpa.UserRepository
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
) {
    fun getChatRooms(userId: Long): List<ChatRoomDto> {
        findUserById(userId)
        val chatRooms = chatRoomRepository.findDistinctAllByParticipantsUserIdOrderByUpdatedDateTimeDesc(userId)
        val unreadCountByChatRoomId = findUnreadCountByChatRoomIdsAndUserId(
            chatRoomIds = chatRooms.map { it.id },
            userId = userId,
        )

        return chatRooms.map { chatRoom ->
            chatRoomMapStruct.toDto(chatRoom).copy(
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
        return chatRoomMapStruct.toDto(chatRoom).copy(
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
                return chatRoomMapStruct.toDto(existingDirectChatRoom)
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

        return chatRoomMapStruct.toDto(chatRoom)
    }

    @Transactional
    fun createMingleChatRoom(mingleId: Long, participantUserIds: Set<Long>): ChatRoomDto {
        val existingChatRoom = chatRoomRepository.findByMingleId(mingleId)
        if (existingChatRoom != null) {
            return chatRoomMapStruct.toDto(existingChatRoom)
        }

        val mingle = mingleService.findMingleById(mingleId)
        val now = LocalDateTime.now()
        val chatRoom = chatRoomRepository.save(
            ChatRoom(
                name = "Mingle #$mingleId",
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
        return chatRoomMapStruct.toDto(chatRoom)
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
        return chatRoomMapStruct.toDto(reloaded)
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
}
