package semo.backend.repository.jpa

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import semo.backend.entity.ChatMessage

@Repository
interface ChatMessageRepository : JpaRepository<ChatMessage, Long> {
    @EntityGraph(attributePaths = ["senderUser"])
    fun findAllByChatRoomIdOrderByCreatedDateTimeAsc(chatRoomId: Long): List<ChatMessage>

    @Query(
        """
        select
            message.chatRoom.id as chatRoomId,
            count(message.id) as unreadCount
        from ChatMessage message
        join ChatParticipant participant
            on participant.chatRoom.id = message.chatRoom.id
            and participant.user.id = :userId
        where message.chatRoom.id in :chatRoomIds
            and message.senderUser.id <> :userId
            and message.createdDateTime > coalesce(participant.lastReadDateTime, participant.joinedDateTime)
        group by message.chatRoom.id
        """,
    )
    fun countUnreadMessagesByChatRoomIdsAndUserId(
        chatRoomIds: Collection<Long>,
        userId: Long,
    ): List<UnreadChatMessageCountProjection>
}

interface UnreadChatMessageCountProjection {
    val chatRoomId: Long
    val unreadCount: Long
}
