package semo.backend.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import semo.backend.enums.QuickMatchResponseStatus
import java.time.LocalDateTime

@Entity
@Table(name = "quick_match_responses")
class QuickMatchResponse(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quick_match_id", nullable = false)
    var quickMatch: QuickMatch,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responder_user_id", nullable = false)
    var responderUser: User,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    var status: QuickMatchResponseStatus,

    @Column(name = "created_date_time", nullable = false)
    var createdDateTime: LocalDateTime,

    @Column(name = "updated_date_time", nullable = false)
    var updatedDateTime: LocalDateTime,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
}
