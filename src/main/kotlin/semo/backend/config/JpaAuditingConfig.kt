package semo.backend.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.security.core.context.SecurityContextHolder
import java.util.Optional

@Configuration
@EnableJpaAuditing
class JpaAuditingConfig {
    @Bean
    fun auditorAware(): AuditorAware<String> {
        return AuditorAware {
            val principal = SecurityContextHolder.getContext().authentication?.principal as? Long
            Optional.of(principal?.toString() ?: SYSTEM_AUDITOR)
        }
    }

    companion object {
        private const val SYSTEM_AUDITOR = "system"
    }
}
