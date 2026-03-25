package semo.backend.controller.request

data class CreateKeywordRequest(
    val label: String,
    val priority: Int,
)
