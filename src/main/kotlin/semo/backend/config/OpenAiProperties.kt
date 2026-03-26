package semo.backend.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.openai")
data class OpenAiProperties(
    var apiKey: String = "",
    var translationModel: String = "gpt-5-mini",
)
