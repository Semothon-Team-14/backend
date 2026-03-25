package semo.backend.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table

@Entity
@Table(name = "keywords")
class Keyword(
    @Column(nullable = false, unique = true)
    var label: String,

    @Column(nullable = false)
    var priority: Int,

    @ManyToMany(mappedBy = "keywords")
    var users: MutableSet<User> = mutableSetOf(),
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
}
