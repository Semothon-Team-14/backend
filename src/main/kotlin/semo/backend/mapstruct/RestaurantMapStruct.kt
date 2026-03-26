package semo.backend.mapstruct

import org.mapstruct.Mapper
import org.mapstruct.MappingConstants
import semo.backend.dto.RestaurantDto
import semo.backend.entity.Restaurant

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
interface RestaurantMapStruct : GenericMapStruct<Restaurant, RestaurantDto> {
    override fun toDto(entity: Restaurant): RestaurantDto {
        return RestaurantDto(
            id = entity.id,
            cityId = entity.city.id,
            name = entity.name,
            phoneNumber = entity.phoneNumber,
            address = entity.address,
            foodCategory = entity.foodCategory,
            latitude = entity.latitude,
            longitude = entity.longitude,
        )
    }
}
