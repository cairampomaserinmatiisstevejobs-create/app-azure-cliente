package com.empresa.cliente.router;

import com.empresa.cliente.handler.ClienteHandler;
import com.empresa.cliente.handler.GuineaHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class ClienteRouter {

    @Bean
    public RouterFunction<ServerResponse> routes(ClienteHandler clienteHandler,
                                                  GuineaHandler guineaHandler) {
        return RouterFunctions.route()
                // Clientes propios
                .GET("/api/clientes",          clienteHandler::listar)
                .GET("/api/clientes/{id}",     clienteHandler::buscarPorId)
                .POST("/api/clientes",         clienteHandler::crear)
                .PUT("/api/clientes/{id}",     clienteHandler::actualizar)
                .DELETE("/api/clientes/{id}",  clienteHandler::eliminar)
                // Guinea (API externa via APIM + OAuth2)
                .GET("/api/guinea/productos",        guineaHandler::listarProductos)
                .GET("/api/guinea/productos/{id}",   guineaHandler::obtenerProducto)
                .build();
    }
}
