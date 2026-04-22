package com.empresa.cliente.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.ClientCredentialsReactiveOAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Configuration
public class OAuth2WebClientConfig {

    /**
     * Manager para M2M (sin usuario, sin ServerWebExchange).
     * AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager funciona en
     * schedulers, batch jobs y service-to-service — no necesita contexto HTTP.
     */
    @Bean
    public ReactiveOAuth2AuthorizedClientManager authorizedClientManager(
            ReactiveClientRegistrationRepository clientRegistrationRepository,
            ReactiveOAuth2AuthorizedClientService authorizedClientService) {

        // Renueva el token 60 segundos antes de que expire (token dura 1 hora)
        ClientCredentialsReactiveOAuth2AuthorizedClientProvider ccProvider =
                new ClientCredentialsReactiveOAuth2AuthorizedClientProvider();
        ccProvider.setClockSkew(Duration.ofSeconds(60));

        var provider = ReactiveOAuth2AuthorizedClientProviderBuilder.builder()
                .provider(ccProvider)
                .build();

        var manager = new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(
                clientRegistrationRepository, authorizedClientService);
        manager.setAuthorizedClientProvider(provider);
        return manager;
    }

    /**
     * WebClient que inyecta el Bearer token automáticamente en cada request.
     * En local (guinea.oauth.enabled=false) el filtro OAuth2 está desactivado.
     * En AKS con variables reales, el token se obtiene de Entra ID y se cachea.
     */
    @Bean
    public WebClient guineaWebClient(
            ReactiveOAuth2AuthorizedClientManager authorizedClientManager,
            @Value("${guinea.apim.url}") String guineaUrl,
            @Value("${guinea.apim.subscription-key}") String subscriptionKey,
            @Value("${guinea.oauth.enabled:false}") boolean oauthEnabled) {

        var builder = WebClient.builder()
                .baseUrl(guineaUrl)
                .defaultHeader("Ocp-Apim-Subscription-Key", subscriptionKey);

        if (oauthEnabled) {
            var oauth2Filter = new ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
            oauth2Filter.setDefaultClientRegistrationId("guinea-api");
            builder.filter(oauth2Filter);
        }

        return builder.build();
    }
}
