package semo.backend.mapstruct

import semo.backend.dto.UserDto
import semo.backend.entity.User


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
interface UserMapStruct : GenericMapStruct<User, UserDto> {
    override fun toDto(entity: User): UserDto
}
