package semo.backend.dto

import semo.backend.enums.QuickMatchStatus
import semo.backend.enums.QuickMatchTargetType
import java.time.LocalDateTime

data class QuickMatchDto(
    val id: Long,
    val requesterUserId: Long,
    val cityId: Long,
    val message: String?,
    val targetType: QuickMatchTargetType,
    val status: QuickMatchStatus,
    val acceptedByUserId: Long?,
    val mingleId: Long?,
    val createdDateTime: LocalDateTime,
    val updatedDateTime: LocalDateTime,
)
