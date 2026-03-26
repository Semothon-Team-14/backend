package semo.backend.mapstruct

import org.mapstruct.Mapper
import org.mapstruct.MappingConstants
import semo.backend.dto.RestaurantImageDto
import semo.backend.entity.RestaurantImage

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
interface RestaurantImageMapStruct : GenericMapStruct<RestaurantImage, RestaurantImageDto> {
    override fun toDto(entity: RestaurantImage): RestaurantImageDto {
        return RestaurantImageDto(
            id = entity.id,
            restaurantId = entity.restaurant.id,
            imageUrl = entity.imageUrl,
            mainImage = entity.mainImage,
        )
    }
}
