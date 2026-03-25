package semo.backend.controller.response

import semo.backend.dto.KeywordDto

data class GetKeywordsResponse(
    val keywords: List<KeywordDto>,
)
