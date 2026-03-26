package semo.backend.repository.jpa

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import semo.backend.entity.ChatRoom

@Repository
interface ChatRoomRepository : JpaRepository<ChatRoom, Long> {
    @EntityGraph(attributePaths = ["participants", "participants.user"])
    fun findDistinctAllByParticipantsUserIdOrderByUpdatedDateTimeDesc(userId: Long): List<ChatRoom>

    @EntityGraph(attributePaths = ["participants", "participants.user"])
    fun findByIdAndParticipantsUserId(
        id: Long,
        userId: Long,
    ): ChatRoom?

    @EntityGraph(attributePaths = ["participants", "participants.user"])
    @Query(
        """
        select distinct chatRoom
        from ChatRoom chatRoom
        join chatRoom.participants participantOne
        join chatRoom.participants participantTwo
        where chatRoom.directChat = true
          and participantOne.user.id = :firstUserId
          and participantTwo.user.id = :secondUserId
        """,
    )
    fun findDirectChatRoomBetweenUsers(
        @Param("firstUserId") firstUserId: Long,
        @Param("secondUserId") secondUserId: Long,
    ): ChatRoom?
}
