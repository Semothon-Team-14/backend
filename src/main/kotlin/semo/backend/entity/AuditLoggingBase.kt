package semo.backend.entity

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class AuditLoggingBase {
    @Column(name = "created_by", nullable = false, length = 10)
    @CreatedBy
    lateinit var createdBy: String

    @Column(name = "created_date_time", nullable = false)
    @CreatedDate
    lateinit var createdDateTime: LocalDateTime

    @Column(name = "updated_by", nullable = false, length = 10)
    @LastModifiedBy
    lateinit var updatedBy: String

    @Column(name = "updated_date_time", nullable = false)
    @LastModifiedDate
    lateinit var updatedDateTime: LocalDateTime
}
