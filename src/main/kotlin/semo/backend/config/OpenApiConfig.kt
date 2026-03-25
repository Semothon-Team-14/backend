package semo.backend.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {
    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("Semo Backend API")
                    .version("v1"),
            )
            .components(
                Components().addSecuritySchemes(
                    ACCESS_TOKEN_SCHEME,
                    SecurityScheme()
                        .type(SecurityScheme.Type.APIKEY)
                        .`in`(SecurityScheme.In.HEADER)
                        .name("accessToken")
                        .description("Use the accessToken header. The special token 'master' is also accepted."),
                ),
            )
            .addSecurityItem(SecurityRequirement().addList(ACCESS_TOKEN_SCHEME))
    }

    companion object {
        const val ACCESS_TOKEN_SCHEME = "accessToken"
    }
}
