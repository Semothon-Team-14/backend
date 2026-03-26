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

@Entity
@Table(name = "cafes")
class Cafe(
    @Column(name = "name", nullable = false)
    var name: String,

    @Column(name = "phone_number")
    var phoneNumber: String?,

    @Column(name = "address")
    var address: String?,

    @Column(name = "food_category")
    var foodCategory: String?,

    @Column(name = "latitude", precision = 10, scale = 7)
    var latitude: BigDecimal?,

    @Column(name = "longitude", precision = 10, scale = 7)
    var longitude: BigDecimal?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    var city: City,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
}
