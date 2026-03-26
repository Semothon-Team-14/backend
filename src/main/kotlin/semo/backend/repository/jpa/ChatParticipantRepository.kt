package semo.backend.repository.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import semo.backend.entity.ChatParticipant

@Repository
interface ChatParticipantRepository : JpaRepository<ChatParticipant, Long> {
    fun existsByChatRoomIdAndUserId(
        chatRoomId: Long,
        userId: Long,
    ): Boolean
}
