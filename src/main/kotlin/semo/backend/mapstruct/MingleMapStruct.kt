package semo.backend.mapstruct

import org.springframework.stereotype.Component
import semo.backend.dto.MingleDto
import semo.backend.entity.Mingle

@Component
class MingleMapStruct(
    private val cityMapStruct: CityMapStruct,
) {
    fun toDto(entity: Mingle): MingleDto {
        return MingleDto(
            id = entity.id,
            city = cityMapStruct.toDto(entity.city),
            title = entity.title,
            description = entity.description,
            placeName = entity.placeName,
            meetDateTime = entity.meetDateTime,
            latitude = entity.latitude,
            longitude = entity.longitude,
            targetParticipantCount = entity.targetParticipantCount,
            createdDateTime = entity.createdDateTime,
            updatedDateTime = entity.updatedDateTime,
        )
    }

    fun toDtos(entities: List<Mingle>): List<MingleDto> {
        return entities.map(::toDto)
    }
}
