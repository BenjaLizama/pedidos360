package com.pedidos360.orders.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

@Configuration
public class AuditConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            // TODO: Cuando implementemos JWT, aquí leeremos el usuario del SecurityContext:
            // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            // if (authentication == null || !authentication.isAuthenticated()) {
            //     return Optional.of("SYSTEM");
            // }
            // return Optional.of(authentication.getName());

            // Por ahora, como estamos probando sin seguridad, devolvemos un usuario temporal:
            return Optional.of("TEST_USER");
        };
    }
}
