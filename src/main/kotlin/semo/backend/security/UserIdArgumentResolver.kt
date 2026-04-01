package semo.backend.security

import org.springframework.core.MethodParameter
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import semo.backend.exception.auth.InvalidAccessTokenException

@Component
class UserIdArgumentResolver : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        if (!parameter.hasParameterAnnotation(UserId::class.java)) {
            return false
        }

        val parameterType = parameter.parameterType
        return parameterType == Long::class.javaObjectType || parameterType == java.lang.Long.TYPE
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): Any {
        val principal = SecurityContextHolder.getContext().authentication?.principal
        return principal as? Long ?: throw InvalidAccessTokenException()
    }
}
