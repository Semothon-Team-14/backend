package semo.backend.facade

import org.springframework.stereotype.Service
import semo.backend.controller.request.CreateKeywordRequest
import semo.backend.controller.request.UpdateKeywordRequest
import semo.backend.dto.KeywordDto
import semo.backend.service.KeywordService

@Service
class KeywordFacade(
    private val keywordService: KeywordService,
) {
    fun getKeywords(): List<KeywordDto> {
        return keywordService.getKeywords()
    }

    fun getKeyword(keywordId: Long): KeywordDto {
        return keywordService.getKeyword(keywordId)
    }

    fun createKeyword(request: CreateKeywordRequest): KeywordDto {
        return keywordService.createKeyword(request)
    }

    fun updateKeyword(keywordId: Long, request: UpdateKeywordRequest): KeywordDto {
        return keywordService.updateKeyword(keywordId, request)
    }

    fun deleteKeyword(keywordId: Long): Long {
        return keywordService.deleteKeyword(keywordId)
    }
}
