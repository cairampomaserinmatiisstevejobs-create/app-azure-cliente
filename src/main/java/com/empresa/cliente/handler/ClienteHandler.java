package com.empresa.cliente.handler;

import com.empresa.cliente.dto.ClienteRequest;
import com.empresa.cliente.service.ClienteService;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class ClienteHandler {

    private final ClienteService service;

    public ClienteHandler(ClienteService service) {
        this.service = service;
    }

    public Mono<ServerResponse> listar(ServerRequest req) {
        return ServerResponse.ok().body(service.listarActivos(),
                com.empresa.cliente.dto.ClienteResponse.class);
    }

    public Mono<ServerResponse> buscarPorId(ServerRequest req) {
        Long id = Long.parseLong(req.pathVariable("id"));
        return service.buscarPorId(id)
                .flatMap(c -> ServerResponse.ok().bodyValue(c))
                .onErrorResume(e -> ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> crear(ServerRequest req) {
        return req.bodyToMono(ClienteRequest.class)
                .flatMap(service::crear)
                .flatMap(c -> ServerResponse.status(201).bodyValue(c))
                .onErrorResume(e -> ServerResponse.badRequest()
                        .bodyValue(java.util.Map.of("error", e.getMessage())));
    }

    public Mono<ServerResponse> actualizar(ServerRequest req) {
        Long id = Long.parseLong(req.pathVariable("id"));
        return req.bodyToMono(ClienteRequest.class)
                .flatMap(r -> service.actualizar(id, r))
                .flatMap(c -> ServerResponse.ok().bodyValue(c))
                .onErrorResume(e -> ServerResponse.badRequest()
                        .bodyValue(java.util.Map.of("error", e.getMessage())));
    }

    public Mono<ServerResponse> eliminar(ServerRequest req) {
        Long id = Long.parseLong(req.pathVariable("id"));
        return service.eliminar(id)
                .then(ServerResponse.noContent().build())
                .onErrorResume(e -> ServerResponse.notFound().build());
    }
}
