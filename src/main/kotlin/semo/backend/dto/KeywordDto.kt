package semo.backend.dto

data class KeywordDto(
    val id: Long,
    val label: String,
    val labelEnglish: String?,
    val priority: Int,
)
