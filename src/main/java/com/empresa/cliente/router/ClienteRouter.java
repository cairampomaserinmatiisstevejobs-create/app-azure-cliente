package com.empresa.cliente.router;

import com.empresa.cliente.handler.ClienteHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class ClienteRouter {

    @Bean
    public RouterFunction<ServerResponse> routes(ClienteHandler handler) {
        return RouterFunctions.route()
                .GET("/api/clientes",          handler::listar)
                .GET("/api/clientes/{id}",     handler::buscarPorId)
                .POST("/api/clientes",         handler::crear)
                .PUT("/api/clientes/{id}",     handler::actualizar)
                .DELETE("/api/clientes/{id}",  handler::eliminar)
                .build();
    }
}
