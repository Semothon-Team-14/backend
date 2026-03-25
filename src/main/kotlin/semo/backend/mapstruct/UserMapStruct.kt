package semo.backend.mapstruct

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingConstants
import semo.backend.controller.request.CreateUserRequest
import semo.backend.dto.UserDto
import semo.backend.entity.User

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
interface UserMapStruct : GenericMapStruct<User, UserDto> {
    override fun toDto(entity: User): UserDto

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDateTime", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedDateTime", ignore = true)
    @Mapping(target = "nationality", ignore = true)
    fun toEntity(request: CreateUserRequest): User
}
