package semo.backend.controller.request

data class CreateKeywordRequest(
    val label: String,
    val labelEnglish: String? = null,
    val priority: Int,
)
