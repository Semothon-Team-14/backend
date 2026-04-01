package semo.backend.dto

import java.time.LocalDateTime

data class SavedCafeDto(
    val id: Long,
    val userId: Long,
    val cafe: CafeDto,
    val createdDateTime: LocalDateTime,
    val updatedDateTime: LocalDateTime,
)
