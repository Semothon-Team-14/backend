package semo.backend.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import semo.backend.config.argument.OptionalRequestBody
import semo.backend.controller.request.CreateKeywordRequest
import semo.backend.controller.request.UpdateKeywordRequest
import semo.backend.controller.response.CreateKeywordResponse
import semo.backend.controller.response.DeleteKeywordResponse
import semo.backend.controller.response.GetKeywordResponse
import semo.backend.controller.response.GetKeywordsResponse
import semo.backend.controller.response.UpdateKeywordResponse
import semo.backend.facade.KeywordFacade

@RestController
@RequestMapping("/keywords")
class KeywordController(
    private val keywordFacade: KeywordFacade,
) {
    @GetMapping
    fun getKeywords(): GetKeywordsResponse {
        return GetKeywordsResponse(
            keywords = keywordFacade.getKeywords(),
        )
    }

    @GetMapping("/search")
    fun searchKeywords(
        @RequestParam query: String,
    ): GetKeywordsResponse {
        return GetKeywordsResponse(
            keywords = keywordFacade.searchKeywords(query),
        )
    }

    @GetMapping("/{keywordId}")
    fun getKeyword(
        @PathVariable keywordId: Long,
    ): GetKeywordResponse {
        return GetKeywordResponse(
            keyword = keywordFacade.getKeyword(keywordId),
        )
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createKeyword(
        @RequestBody request: CreateKeywordRequest,
    ): CreateKeywordResponse {
        return CreateKeywordResponse(
            keyword = keywordFacade.createKeyword(request),
        )
    }

    @PutMapping("/{keywordId}")
    fun updateKeyword(
        @PathVariable keywordId: Long,
        @OptionalRequestBody request: UpdateKeywordRequest,
    ): UpdateKeywordResponse {
        return UpdateKeywordResponse(
            keyword = keywordFacade.updateKeyword(keywordId, request),
        )
    }

    @DeleteMapping("/{keywordId}")
    fun deleteKeyword(
        @PathVariable keywordId: Long,
    ): DeleteKeywordResponse {
        return DeleteKeywordResponse(
            deletedKeywordId = keywordFacade.deleteKeyword(keywordId),
        )
    }
}
