package com.empresa.cliente.service;

import com.empresa.cliente.dto.ClienteRequest;
import com.empresa.cliente.dto.ClienteResponse;
import com.empresa.cliente.entity.Cliente;
import com.empresa.cliente.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ClienteService {

    private final ClienteRepository repository;

    public ClienteService(ClienteRepository repository) {
        this.repository = repository;
    }

    public Flux<ClienteResponse> listarActivos() {
        return repository.findByEstado("ACTIVO")
                .map(ClienteResponse::from);
    }

    public Mono<ClienteResponse> buscarPorId(Long id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Cliente no encontrado: " + id)))
                .map(ClienteResponse::from);
    }

    public Mono<ClienteResponse> crear(ClienteRequest request) {
        return repository.existsByEmail(request.email())
                .flatMap(existe -> existe
                        ? Mono.error(new RuntimeException("Email ya registrado: " + request.email()))
                        : repository.save(Cliente.crear(request.nombre(), request.apellido(),
                                                        request.email(), request.telefono()))
                                    .map(ClienteResponse::from));
    }

    public Mono<ClienteResponse> actualizar(Long id, ClienteRequest request) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Cliente no encontrado: " + id)))
                .flatMap(c -> repository.save(new Cliente(
                        c.id(), request.nombre(), request.apellido(),
                        request.email(), request.telefono(),
                        c.estado(), c.createdAt(), java.time.LocalDateTime.now()
                )))
                .map(ClienteResponse::from);
    }

    public Mono<Void> eliminar(Long id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Cliente no encontrado: " + id)))
                .flatMap(c -> repository.save(c.conEstado("INACTIVO")))
                .then();
    }
}
