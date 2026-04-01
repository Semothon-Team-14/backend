package semo.backend.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "chat_rooms")
class ChatRoom(
    @Column(name = "name")
    var name: String?,

    @Column(name = "direct_chat", nullable = false)
    var directChat: Boolean,

    @Column(name = "created_date_time", nullable = false)
    var createdDateTime: LocalDateTime,

    @Column(name = "updated_date_time", nullable = false)
    var updatedDateTime: LocalDateTime,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mingle_id")
    var mingle: Mingle? = null,

    @OneToMany(mappedBy = "chatRoom", fetch = FetchType.LAZY)
    var participants: MutableSet<ChatParticipant> = mutableSetOf(),
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
}
