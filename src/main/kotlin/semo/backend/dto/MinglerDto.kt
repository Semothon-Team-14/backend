package semo.backend.dto

import java.time.LocalDateTime

data class MinglerDto(
    val id: Long,
    val userId: Long,
    val mingle: MingleDto,
    val createdDateTime: LocalDateTime,
    val updatedDateTime: LocalDateTime,
)
