package semo.backend.mapstruct

import org.springframework.stereotype.Component
import semo.backend.dto.QuickMatchDto
import semo.backend.entity.QuickMatch

@Component
class QuickMatchMapStruct {
    fun toDto(entity: QuickMatch): QuickMatchDto {
        return QuickMatchDto(
            id = entity.id,
            requesterUserId = entity.requesterUser.id,
            cityId = entity.city.id,
            message = entity.message,
            targetType = entity.targetType,
            status = entity.status,
            acceptedByUserId = entity.acceptedByUser?.id,
            mingleId = entity.mingle?.id,
            createdDateTime = entity.createdDateTime,
            updatedDateTime = entity.updatedDateTime,
        )
    }

    fun toDtos(entities: List<QuickMatch>): List<QuickMatchDto> {
        return entities.map(::toDto)
    }
}
