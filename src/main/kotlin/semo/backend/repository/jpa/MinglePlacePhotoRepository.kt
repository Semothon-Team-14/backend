package semo.backend.repository.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import semo.backend.entity.MinglePlacePhoto

@Repository
interface MinglePlacePhotoRepository : JpaRepository<MinglePlacePhoto, Long> {
    fun findAllByMingleIdOrderByCreatedDateTimeAsc(mingleId: Long): List<MinglePlacePhoto>

    fun findByIdAndMingleId(
        id: Long,
        mingleId: Long,
    ): MinglePlacePhoto?
}
