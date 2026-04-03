package semo.backend.service

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import semo.backend.config.OpenAiProperties
import semo.backend.exception.openai.OpenAiApiKeyMissingException
import semo.backend.exception.openai.OpenAiTranslationFailedException

@Service
class OpenAiTranslationService(
    restClientBuilder: RestClient.Builder,
    private val openAiProperties: OpenAiProperties,
) {
    private val restClient = restClientBuilder
        .baseUrl(openAiProperties.baseUrl)
        .build()

    fun translateText(
        originalContent: String,
        sourceCountryName: String,
        targetCountryName: String,
    ): String {
        val requestBody = ChatCompletionRequest(
            model = openAiProperties.translationModel,
            messages = listOf(
                ChatCompletionMessage(
                    role = "system",
                    content = """
                    You are a translation assistant for chat messages.
                    Translate the user's message into the primary language used in $targetCountryName.
                    The sender's nationality is from $sourceCountryName.
                    Return only the translated message text with no explanation.
                    Preserve tone, intent, and informal chat style.
                    """.trimIndent(),
                ),
                ChatCompletionMessage(
                    role = "user",
                    content = originalContent,
                ),
            ),
        )

        val response = requestChatCompletion(requestBody)

        return response?.choices
            ?.firstOrNull()
            ?.message
            ?.content
            ?.trim()
            ?.takeIf { it.isNotBlank() }
            ?: throw OpenAiTranslationFailedException()
    }

    fun detectLanguageCode(originalContent: String): String {
        val requestBody = ChatCompletionRequest(
            model = openAiProperties.translationModel,
            messages = listOf(
                ChatCompletionMessage(
                    role = "system",
                    content = """
                    Detect the message language and return only the ISO 639-1 lowercase language code.
                    If uncertain, return your best single guess.
                    """.trimIndent(),
                ),
                ChatCompletionMessage(
                    role = "user",
                    content = originalContent,
                ),
            ),
        )

        val detected = requestChatCompletion(requestBody)
            ?.choices
            ?.firstOrNull()
            ?.message
            ?.content
            ?.trim()
            ?.lowercase()
            ?.take(2)
            ?.takeIf { it.matches(Regex("^[a-z]{2}$")) }
            ?: throw OpenAiTranslationFailedException()

        return detected
    }

    fun primaryLanguageCodeByCountryCode(countryCode: String?): String? {
        if (countryCode.isNullOrBlank()) {
            return null
        }

        return PRIMARY_LANGUAGE_BY_COUNTRY[countryCode.trim().uppercase()]
    }

    private fun requestChatCompletion(requestBody: ChatCompletionRequest): ChatCompletionResponse? {
        val apiKey = openAiProperties.apiKey.trim()
        if (apiKey.isBlank()) {
            throw OpenAiApiKeyMissingException()
        }

        return try {
            restClient.post()
                .uri("/chat/completions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $apiKey")
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(ChatCompletionResponse::class.java)
        } catch (_: Exception) {
            throw OpenAiTranslationFailedException()
        }
    }

    private data class ChatCompletionRequest(
        val model: String,
        val messages: List<ChatCompletionMessage>,
    )

    private data class ChatCompletionMessage(
        val role: String,
        val content: String,
    )

    private data class ChatCompletionResponse(
        val choices: List<ChatCompletionChoice> = emptyList(),
    )

    private data class ChatCompletionChoice(
        val message: ChatCompletionMessageResponse,
    )

    private data class ChatCompletionMessageResponse(
        val content: String?,
    )

    companion object {
        private val PRIMARY_LANGUAGE_BY_COUNTRY = mapOf(
            "KR" to "ko",
            "KP" to "ko",
            "JP" to "ja",
            "CN" to "zh",
            "TW" to "zh",
            "HK" to "zh",
            "MO" to "zh",
            "US" to "en",
            "GB" to "en",
            "AU" to "en",
            "CA" to "en",
            "NZ" to "en",
            "SG" to "en",
            "PH" to "en",
            "IN" to "hi",
            "TH" to "th",
            "VN" to "vi",
            "ID" to "id",
            "MY" to "ms",
            "DE" to "de",
            "FR" to "fr",
            "ES" to "es",
            "IT" to "it",
            "PT" to "pt",
            "BR" to "pt",
            "RU" to "ru",
            "UA" to "uk",
            "TR" to "tr",
            "SA" to "ar",
            "AE" to "ar",
        )
    }
}
