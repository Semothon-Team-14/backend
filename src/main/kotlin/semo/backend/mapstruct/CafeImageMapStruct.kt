package semo.backend.mapstruct

import org.mapstruct.Mapper
import org.mapstruct.MappingConstants
import semo.backend.dto.CafeImageDto
import semo.backend.entity.CafeImage

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
interface CafeImageMapStruct : GenericMapStruct<CafeImage, CafeImageDto> {
    override fun toDto(entity: CafeImage): CafeImageDto {
        return CafeImageDto(
            id = entity.id,
            cafeId = entity.cafe.id,
            imageUrl = entity.imageUrl,
            mainImage = entity.mainImage,
        )
    }
}
