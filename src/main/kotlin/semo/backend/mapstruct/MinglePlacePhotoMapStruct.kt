package semo.backend.mapstruct

import org.mapstruct.Mapper
import org.mapstruct.MappingConstants
import semo.backend.dto.MinglePlacePhotoDto
import semo.backend.entity.MinglePlacePhoto

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
interface MinglePlacePhotoMapStruct : GenericMapStruct<MinglePlacePhoto, MinglePlacePhotoDto> {
    override fun toDto(entity: MinglePlacePhoto): MinglePlacePhotoDto {
        return MinglePlacePhotoDto(
            id = entity.id,
            mingleId = entity.mingle.id,
            imageUrl = entity.imageUrl,
            createdDateTime = entity.createdDateTime,
            updatedDateTime = entity.updatedDateTime,
        )
    }
}
