package com.empresa.cliente.dto;

import com.empresa.cliente.entity.Cliente;

import java.time.LocalDateTime;

public record ClienteResponse(
        Long id,
        String nombre,
        String apellido,
        String email,
        String telefono,
        String estado,
        LocalDateTime createdAt
) {
    public static ClienteResponse from(Cliente c) {
        return new ClienteResponse(c.id(), c.nombre(), c.apellido(),
                c.email(), c.telefono(), c.estado(), c.createdAt());
    }
}
