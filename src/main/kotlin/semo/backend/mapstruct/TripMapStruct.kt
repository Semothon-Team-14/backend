package semo.backend.mapstruct

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingConstants
import semo.backend.controller.request.CreateTripRequest
import semo.backend.dto.TripDto
import semo.backend.entity.Trip

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
interface TripMapStruct : GenericMapStruct<Trip, TripDto> {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "cityId", source = "city.id")
    @Mapping(target = "fromCityId", source = "fromCity.id")
    override fun toDto(entity: Trip): TripDto

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDateTime", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedDateTime", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "city", ignore = true)
    @Mapping(target = "fromCity", ignore = true)
    fun toEntity(request: CreateTripRequest): Trip
}
