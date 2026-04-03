package semo.backend.dto

data class QuickMatchAcceptResultDto(
    val quickMatch: QuickMatchDto,
    val mingle: MingleDto?,
    val chatRoom: ChatRoomDto,
)
