package semo.backend.mapstruct

import org.springframework.stereotype.Component
import semo.backend.dto.LocalDto
import semo.backend.entity.Local

@Component
class LocalMapStruct(
    private val cityMapStruct: CityMapStruct,
) {
    fun toDto(entity: Local): LocalDto {
        return LocalDto(
            id = entity.id,
            userId = entity.user.id,
            city = cityMapStruct.toDto(entity.city),
            availableTimeText = entity.availableTimeText,
            createdDateTime = entity.createdDateTime,
            updatedDateTime = entity.updatedDateTime,
        )
    }

    fun toDtos(entities: List<Local>): List<LocalDto> {
        return entities.map(::toDto)
    }
}
