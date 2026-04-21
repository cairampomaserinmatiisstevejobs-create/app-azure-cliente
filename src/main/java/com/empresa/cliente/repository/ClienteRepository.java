package com.empresa.cliente.repository;

import com.empresa.cliente.entity.Cliente;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ClienteRepository extends R2dbcRepository<Cliente, Long> {

    Flux<Cliente> findByEstado(String estado);

    Mono<Cliente> findByEmail(String email);

    Mono<Boolean> existsByEmail(String email);
}
