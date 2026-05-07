package com.newbie.app.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

/**
 * JPA configuration enabling automatic entity auditing.
 *
 * <p>The {@code AuditorAware<String>} bean provides the current actor for
 * {@code @CreatedBy} and {@code @LastModifiedBy} fields. The default implementation
 * returns {@code "system"} as a placeholder.</p>
 *
 * <p><b>To integrate with Spring Security:</b> replace the bean body with:
 * <pre>{@code
 * return () -> Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
 *         .filter(Authentication::isAuthenticated)
 *         .map(Authentication::getName);
 * }</pre>
 * </p>
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class JpaConfig {

    @Bean
    public AuditorAware<String> auditorAware() {
        // Default: returns "system" until Spring Security is wired in.
        // Replace with SecurityContextHolder-based implementation when auth is added.
        return () -> Optional.of("system");
    }
}
