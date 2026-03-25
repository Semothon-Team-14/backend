package semo.backend.mapstruct

import org.mapstruct.Mapper
import org.mapstruct.MappingConstants
import semo.backend.dto.KeywordDto
import semo.backend.entity.Keyword

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
interface KeywordMapStruct : GenericMapStruct<Keyword, KeywordDto> {
    override fun toDto(entity: Keyword): KeywordDto
}
