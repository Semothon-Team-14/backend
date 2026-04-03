package semo.backend.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "mingles")
class Mingle(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    var city: City,

    @Column(name = "title", nullable = false, length = 100)
    var title: String,

    @Column(name = "description", length = 1000)
    var description: String? = null,

    @Column(name = "place_name", length = 255)
    var placeName: String? = null,

    @Column(name = "meet_date_time")
    var meetDateTime: LocalDateTime? = null,

    @Column(name = "latitude", precision = 10, scale = 7)
    var latitude: BigDecimal? = null,

    @Column(name = "longitude", precision = 10, scale = 7)
    var longitude: BigDecimal? = null,
) : AuditLoggingBase() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
}
