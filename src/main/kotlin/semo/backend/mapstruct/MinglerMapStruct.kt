package semo.backend.mapstruct

import org.springframework.stereotype.Component
import semo.backend.dto.MinglerDto
import semo.backend.entity.Mingler
import semo.backend.repository.jpa.LocalRepository

@Component
class MinglerMapStruct(
    private val mingleMapStruct: MingleMapStruct,
    private val localRepository: LocalRepository,
) {
    fun toDto(entity: Mingler): MinglerDto {
        val mingleCityId = entity.mingle.city.id
        val minglerUserId = entity.user.id

        return MinglerDto(
            id = entity.id,
            userId = entity.user.id,
            local = localRepository.existsByUserIdAndCityId(minglerUserId, mingleCityId),
            mingle = mingleMapStruct.toDto(entity.mingle),
            createdDateTime = entity.createdDateTime,
            updatedDateTime = entity.updatedDateTime,
        )
    }

    fun toDtos(entities: List<Mingler>): List<MinglerDto> {
        return entities.map(::toDto)
    }
}
