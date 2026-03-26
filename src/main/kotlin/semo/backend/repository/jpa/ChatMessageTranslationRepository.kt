package semo.backend.repository.jpa

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import semo.backend.entity.ChatMessageTranslation

@Repository
interface ChatMessageTranslationRepository : JpaRepository<ChatMessageTranslation, Long> {
    @EntityGraph(attributePaths = ["chatMessage", "user"])
    fun findByChatMessageIdAndUserId(
        chatMessageId: Long,
        userId: Long,
    ): ChatMessageTranslation?
}
