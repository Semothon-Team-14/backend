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
        val apiKey = openAiProperties.apiKey.trim()
        if (apiKey.isBlank()) {
            throw OpenAiApiKeyMissingException()
        }

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

        val response = try {
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

        return response?.choices
            ?.firstOrNull()
            ?.message
            ?.content
            ?.trim()
            ?.takeIf { it.isNotBlank() }
            ?: throw OpenAiTranslationFailedException()
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
}
