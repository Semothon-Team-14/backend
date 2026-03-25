package semo.backend.mapstruct

import org.mapstruct.Mapper
import org.mapstruct.MappingConstants
import semo.backend.dto.NationalityDto
import semo.backend.entity.Nationality

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
interface NationalityMapStruct : GenericMapStruct<Nationality, NationalityDto> {
    override fun toDto(entity: Nationality): NationalityDto
}
