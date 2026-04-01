package semo.backend.mapstruct

import org.springframework.stereotype.Component
import semo.backend.dto.SavedRestaurantDto
import semo.backend.entity.SavedRestaurant

@Component
class SavedRestaurantMapStruct(
    private val restaurantMapStruct: RestaurantMapStruct,
) {
    fun toDto(entity: SavedRestaurant): SavedRestaurantDto {
        return SavedRestaurantDto(
            id = entity.id,
            userId = entity.user.id,
            restaurant = restaurantMapStruct.toDto(entity.restaurant),
            createdDateTime = entity.createdDateTime,
            updatedDateTime = entity.updatedDateTime,
        )
    }

    fun toDtos(entities: List<SavedRestaurant>): List<SavedRestaurantDto> {
        return entities.map(::toDto)
    }
}
