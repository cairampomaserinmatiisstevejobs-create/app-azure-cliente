package com.empresa.cliente.handler;

import com.empresa.cliente.service.GuineaService;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class GuineaHandler {

    private final GuineaService guineaService;

    public GuineaHandler(GuineaService guineaService) {
        this.guineaService = guineaService;
    }

    public Mono<ServerResponse> listarProductos(ServerRequest request) {
        return ServerResponse.ok()
                .body(guineaService.listarProductos(), GuineaService.GuineaProducto.class);
    }

    public Mono<ServerResponse> obtenerProducto(ServerRequest request) {
        Long id = Long.parseLong(request.pathVariable("id"));
        return guineaService.obtenerProducto(id)
                .flatMap(producto -> ServerResponse.ok().bodyValue(producto))
                .onErrorResume(RuntimeException.class, e ->
                        ServerResponse.notFound().build());
    }
}
