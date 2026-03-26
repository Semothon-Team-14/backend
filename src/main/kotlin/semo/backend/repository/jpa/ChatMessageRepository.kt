package semo.backend.repository.jpa

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import semo.backend.entity.ChatMessage

@Repository
interface ChatMessageRepository : JpaRepository<ChatMessage, Long> {
    @EntityGraph(attributePaths = ["senderUser"])
    fun findAllByChatRoomIdOrderByCreatedDateTimeAsc(chatRoomId: Long): List<ChatMessage>
}
