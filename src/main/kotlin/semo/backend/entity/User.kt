package semo.backend.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
class User (
    @Column
    var username: String,

    @Column
    var password: String,

    @Column
    var name: String,

    @Column
    var email: String,

    @Column
    var phone: String,

): AuditLoggingBase(){
    @Id
    var id : Long = 0
}