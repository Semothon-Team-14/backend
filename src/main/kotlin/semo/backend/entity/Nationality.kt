package semo.backend.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "nationalities")
class Nationality(
    @Column(name = "country_code", nullable = false, length = 2, unique = true)
    var countryCode: String,

    @Column(name = "country_name_english", nullable = false)
    var countryNameEnglish: String,

    @Column(name = "country_name_korean", nullable = false)
    var countryNameKorean: String,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
}
