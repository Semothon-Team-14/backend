package semo.backend.mapstruct

import org.springframework.stereotype.Component
import semo.backend.dto.MinglerDto
import semo.backend.entity.Mingler

@Component
class MinglerMapStruct(
    private val mingleMapStruct: MingleMapStruct,
) {
    fun toDto(entity: Mingler): MinglerDto {
        return MinglerDto(
            id = entity.id,
            userId = entity.user.id,
            mingle = mingleMapStruct.toDto(entity.mingle),
            createdDateTime = entity.createdDateTime,
            updatedDateTime = entity.updatedDateTime,
        )
    }

    fun toDtos(entities: List<Mingler>): List<MinglerDto> {
        return entities.map(::toDto)
    }
}
