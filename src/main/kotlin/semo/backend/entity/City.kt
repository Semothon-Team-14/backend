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
@Table(name = "cities")
class City(
    @Column(name = "city_name_english", nullable = false)
    var cityNameEnglish: String,

    @Column(name = "city_name_korean", nullable = false)
    var cityNameKorean: String,

    @Column(name = "representative_image_url")
    var representativeImageUrl: String? = null,

    @Column(name = "center_latitude", precision = 10, scale = 7)
    var centerLatitude: BigDecimal? = null,

    @Column(name = "center_longitude", precision = 10, scale = 7)
    var centerLongitude: BigDecimal? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nationality_id", nullable = false)
    var nationality: Nationality,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
}
