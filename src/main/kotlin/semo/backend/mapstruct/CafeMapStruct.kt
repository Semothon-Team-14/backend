package semo.backend.mapstruct

import org.mapstruct.Mapper
import org.mapstruct.MappingConstants
import semo.backend.dto.CafeDto
import semo.backend.entity.Cafe

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
interface CafeMapStruct : GenericMapStruct<Cafe, CafeDto> {
    override fun toDto(entity: Cafe): CafeDto {
        return CafeDto(
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
