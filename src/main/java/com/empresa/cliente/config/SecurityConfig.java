package com.empresa.cliente.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Este microservicio NO protege sus propios endpoints con OAuth2
 * (eso lo hace APIM hacia afuera).
 *
 * La dependencia spring-boot-starter-oauth2-client se usa SOLO para
 * obtener tokens y llamar a APIs externas (Guinea via APIM) — no para
 * proteger nuestros propios endpoints.
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .anyExchange().permitAll()
                )
                .build();
    }
}
