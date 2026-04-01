package semo.backend.mapstruct

import org.springframework.stereotype.Component
import semo.backend.dto.SavedCafeDto
import semo.backend.entity.SavedCafe

@Component
class SavedCafeMapStruct(
    private val cafeMapStruct: CafeMapStruct,
) {
    fun toDto(entity: SavedCafe): SavedCafeDto {
        return SavedCafeDto(
            id = entity.id,
            userId = entity.user.id,
            cafe = cafeMapStruct.toDto(entity.cafe),
            createdDateTime = entity.createdDateTime,
            updatedDateTime = entity.updatedDateTime,
        )
    }

    fun toDtos(entities: List<SavedCafe>): List<SavedCafeDto> {
        return entities.map(::toDto)
    }
}
