package semo.backend.mapstruct

interface GenericMapStruct<E, D> {
    fun toDto(entity: E): D

    fun toDtos(entities: List<E>): List<D> {
        return entities.map(::toDto)
    }
}
