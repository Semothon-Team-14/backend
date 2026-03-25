package semo.backend.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import semo.backend.security.UserIdArgumentResolver

@Configuration
class WebMvcConfig(
    private val userIdArgumentResolver: UserIdArgumentResolver,
) : WebMvcConfigurer {
    override fun addArgumentResolvers(argumentResolvers: MutableList<HandlerMethodArgumentResolver>) {
        argumentResolvers.add(userIdArgumentResolver)
    }
}
