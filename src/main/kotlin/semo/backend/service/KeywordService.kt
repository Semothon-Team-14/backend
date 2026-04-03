package semo.backend.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import semo.backend.controller.request.CreateKeywordRequest
import semo.backend.controller.request.UpdateKeywordRequest
import semo.backend.dto.KeywordDto
import semo.backend.entity.Keyword
import semo.backend.exception.keyword.DuplicateKeywordLabelException
import semo.backend.exception.keyword.InvalidKeywordPriorityException
import semo.backend.exception.keyword.KeywordNotFoundException
import semo.backend.mapstruct.KeywordMapStruct
import semo.backend.repository.jpa.KeywordRepository
import semo.backend.util.applyIfProvided

@Service
@Transactional(readOnly = true)
class KeywordService(
    private val keywordRepository: KeywordRepository,
    private val keywordMapStruct: KeywordMapStruct,
) {
    fun getKeywords(): List<KeywordDto> {
        return keywordMapStruct.toDtos(keywordRepository.findAllByOrderByPriorityAscLabelAsc())
    }

    fun searchKeywords(query: String): List<KeywordDto> {
        return keywordMapStruct.toDtos(keywordRepository.searchByLabelOrderByPriority(query.trim()))
    }

    fun getKeyword(keywordId: Long): KeywordDto {
        return keywordMapStruct.toDto(findKeywordById(keywordId))
    }

    @Transactional
    fun createKeyword(request: CreateKeywordRequest): KeywordDto {
        val label = normalizeLabel(request.label)
        val labelEnglish = normalizeOptionalLabel(request.labelEnglish)
        validatePriority(request.priority)
        ensureLabelAvailable(label)
        ensureLabelEnglishAvailable(labelEnglish)
        val keyword = Keyword(
            label = label,
            labelEnglish = labelEnglish,
            priority = request.priority,
        )
        return keywordMapStruct.toDto(keywordRepository.save(keyword))
    }

    @Transactional
    fun updateKeyword(keywordId: Long, request: UpdateKeywordRequest): KeywordDto {
        val keyword = findKeywordById(keywordId)
        request.label.applyIfProvided { label ->
            val normalizedLabel = label?.let(::normalizeLabel)
            ensureLabelAvailable(normalizedLabel, keywordId)
            keyword.label = normalizedLabel ?: keyword.label
        }
        request.labelEnglish.applyIfProvided { labelEnglish ->
            val normalizedLabelEnglish = normalizeOptionalLabel(labelEnglish)
            ensureLabelEnglishAvailable(normalizedLabelEnglish, keywordId)
            keyword.labelEnglish = normalizedLabelEnglish
        }
        request.priority.applyIfProvided { priority ->
            if (priority != null) {
                validatePriority(priority)
                keyword.priority = priority
            }
        }
        return keywordMapStruct.toDto(keywordRepository.save(keyword))
    }

    @Transactional
    fun deleteKeyword(keywordId: Long): Long {
        val keyword = findKeywordById(keywordId)
        keywordRepository.delete(keyword)
        return keywordId
    }

    private fun findKeywordById(keywordId: Long): Keyword {
        return keywordRepository.findById(keywordId)
            .orElseThrow { KeywordNotFoundException(listOf(keywordId)) }
    }

    private fun ensureLabelAvailable(
        label: String?,
        keywordId: Long? = null,
    ) {
        if (label == null) {
            return
        }

        val exists = if (keywordId == null) {
            keywordRepository.existsByLabel(label)
        } else {
            keywordRepository.existsByLabelAndIdNot(label, keywordId)
        }

        if (exists) {
            throw DuplicateKeywordLabelException(label)
        }
    }

    private fun validatePriority(priority: Int) {
        if (priority !in 1..10) {
            throw InvalidKeywordPriorityException(priority)
        }
    }

    private fun ensureLabelEnglishAvailable(
        labelEnglish: String?,
        keywordId: Long? = null,
    ) {
        if (labelEnglish == null) {
            return
        }

        val exists = if (keywordId == null) {
            keywordRepository.existsByLabelEnglish(labelEnglish)
        } else {
            keywordRepository.existsByLabelEnglishAndIdNot(labelEnglish, keywordId)
        }

        if (exists) {
            throw DuplicateKeywordLabelException(labelEnglish)
        }
    }

    private fun normalizeLabel(label: String): String {
        return label.trim()
    }

    private fun normalizeOptionalLabel(label: String?): String? {
        val normalized = label?.trim()
        return normalized?.takeIf { it.isNotEmpty() }
    }
}
