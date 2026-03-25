package semo.backend.mapstruct

import org.mapstruct.Mapper
import org.mapstruct.MappingConstants
import semo.backend.dto.CityDto
import semo.backend.entity.City

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
interface CityMapStruct : GenericMapStruct<City, CityDto> {
    override fun toDto(entity: City): CityDto {
        return CityDto(
            id = entity.id,
            nationalityId = entity.nationality.id,
            cityNameEnglish = entity.cityNameEnglish,
            cityNameKorean = entity.cityNameKorean,
        )
    }
}
