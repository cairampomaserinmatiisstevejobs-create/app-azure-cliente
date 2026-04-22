package com.empresa.cliente.service;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Servicio que consume la API de Guinea a través de APIM.
 *
 * En AKS (guinea.oauth.enabled=true):
 *   → WebClient inyecta Bearer token automáticamente (Client Credentials Flow)
 *   → Header Ocp-Apim-Subscription-Key va siempre
 *   → APIM valida ambos: subscription-key + JWT de Entra ID
 *
 * En local (guinea.oauth.enabled=false):
 *   → Apunta a jsonplaceholder.typicode.com como mock de Guinea
 *   → No necesita token real
 */
@Service
public class GuineaService {

    private final WebClient guineaWebClient;

    public GuineaService(WebClient guineaWebClient) {
        this.guineaWebClient = guineaWebClient;
    }

    /**
     * Lista productos de Guinea — simula GET /productos en APIM.
     * En local usa /posts de jsonplaceholder como datos de prueba.
     */
    public Flux<GuineaProducto> listarProductos() {
        return guineaWebClient.get()
                .uri("/posts")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, resp ->
                        resp.bodyToMono(String.class)
                            .flatMap(body -> Mono.error(
                                new RuntimeException("Guinea 4xx: " + body))))
                .onStatus(HttpStatusCode::is5xxServerError, resp ->
                        resp.bodyToMono(String.class)
                            .flatMap(body -> Mono.error(
                                new RuntimeException("Guinea 5xx: " + body))))
                .bodyToFlux(GuineaProducto.class);
    }

    /**
     * Obtiene un producto por ID — simula GET /productos/{id} en APIM.
     */
    public Mono<GuineaProducto> obtenerProducto(Long id) {
        return guineaWebClient.get()
                .uri("/posts/{id}", id)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, resp ->
                        Mono.error(new RuntimeException("Producto no encontrado en Guinea: " + id)))
                .bodyToMono(GuineaProducto.class);
    }

    /**
     * DTO que mapea la respuesta de Guinea (jsonplaceholder /posts en local).
     * En producción reemplazar con el DTO real de Guinea.
     */
    public record GuineaProducto(
            Long id,
            Long userId,
            String title,
            String body
    ) {}
}
