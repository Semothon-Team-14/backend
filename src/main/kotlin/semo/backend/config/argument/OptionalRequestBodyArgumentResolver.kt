package semo.backend.config.argument

import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import semo.backend.exception.request.InvalidRequestBodyException
import tools.jackson.databind.ObjectMapper

@Component
class OptionalRequestBodyArgumentResolver(
    private val objectMapper: ObjectMapper,
) : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(OptionalRequestBody::class.java)
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): Any {
        val request = webRequest.nativeRequest as? jakarta.servlet.http.HttpServletRequest
            ?: throw InvalidRequestBodyException()
        val requestBody = request.reader.use { it.readText() }
        if (requestBody.isBlank()) {
            throw InvalidRequestBodyException()
        }
        return try {
            objectMapper.readValue(requestBody, parameter.nestedParameterType)
        } catch (_: Exception) {
            throw InvalidRequestBodyException()
        }
    }
}
