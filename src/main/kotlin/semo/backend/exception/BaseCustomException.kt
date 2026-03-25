package semo.backend.exception

import org.springframework.http.HttpStatus

abstract class BaseCustomException(
    val status: HttpStatus,
    private val reasonTemplate: String,
    open val reasonVariables: Map<String, Any?> = emptyMap(),
) : RuntimeException(renderReason(reasonTemplate, reasonVariables)) {
    val reason: String = renderReason(reasonTemplate, reasonVariables)

    companion object {
        private val templatePattern = Regex("\\{([^}]+)}")

        private fun renderReason(
            reasonTemplate: String,
            reasonVariables: Map<String, Any?>,
        ): String {
            return templatePattern.replace(reasonTemplate) { matchResult ->
                val key = matchResult.groupValues[1]
                reasonVariables[key]?.toString() ?: matchResult.value
            }
        }
    }
}
