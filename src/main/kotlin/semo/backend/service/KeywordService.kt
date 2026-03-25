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
import java.util.Optional

@Service
class KeywordService(
    private val keywordRepository: KeywordRepository,
    private val keywordMapStruct: KeywordMapStruct,
) {
    fun getKeywords(): List<KeywordDto> {
        return keywordMapStruct.toDtos(
            keywordRepository.findAll().sortedWith(compareBy<Keyword> { it.priority }.thenBy { it.label }),
        )
    }

    fun getKeyword(keywordId: Long): KeywordDto {
        return keywordMapStruct.toDto(findKeywordById(keywordId))
    }

    @Transactional
    fun createKeyword(request: CreateKeywordRequest): KeywordDto {
        val label = normalizeLabel(request.label)
        validatePriority(request.priority)
        ensureLabelAvailable(label)
        val keyword = Keyword(
            label = label,
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

    private fun normalizeLabel(label: String): String {
        return label.trim()
    }

    private inline fun <T> Optional<T>?.applyIfProvided(
        block: (T?) -> Unit,
    ) {
        if (this != null) {
            block(orElse(null))
        }
    }
}
