package semo.backend.mapstruct

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingConstants
import org.springframework.beans.factory.annotation.Autowired
import semo.backend.controller.request.CreateUserRequest
import semo.backend.dto.UserDto
import semo.backend.entity.Keyword
import semo.backend.entity.User

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
abstract class UserMapStruct : GenericMapStruct<User, UserDto> {
    @Autowired
    protected lateinit var keywordMapStruct: KeywordMapStruct

    override fun toDto(entity: User): UserDto {
        return UserDto(
            id = entity.id,
            username = entity.username,
            name = entity.name,
            email = entity.email,
            phone = entity.phone,
            introduction = entity.introduction,
            nationalityId = entity.nationality?.id,
            keywords = keywordMapStruct.toDtos(
                entity.keywords
                    .sortedWith(compareBy<Keyword> { it.priority }.thenBy { it.label }),
            ),
        )
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDateTime", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedDateTime", ignore = true)
    @Mapping(target = "nationality", ignore = true)
    @Mapping(target = "keywords", ignore = true)
    abstract fun toEntity(request: CreateUserRequest): User
}
