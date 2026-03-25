package semo.backend.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToOne
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table

@Entity
@Table(name = "users")
class User(
    @Column
    var username: String?,

    @Column
    var password: String?,

    @Column
    var name: String?,

    @Column
    var email: String?,

    @Column
    var phone: String?,

    @Column(columnDefinition = "TEXT")
    var introduction: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nationality_id")
    var nationality: Nationality? = null,

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_keywords",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "keyword_id")],
    )
    var keywords: MutableSet<Keyword> = mutableSetOf(),

): AuditLoggingBase() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
}
