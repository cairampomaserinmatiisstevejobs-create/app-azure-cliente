package com.empresa.cliente.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("cliente")
public record Cliente(
        @Id
        Long id,

        String nombre,
        String apellido,
        String email,
        String telefono,
        String estado,

        @Column("created_at")
        LocalDateTime createdAt,

        @Column("updated_at")
        LocalDateTime updatedAt
) {
    public static Cliente crear(String nombre, String apellido, String email, String telefono) {
        return new Cliente(null, nombre, apellido, email, telefono,
                "ACTIVO", LocalDateTime.now(), LocalDateTime.now());
    }

    public Cliente conEstado(String nuevoEstado) {
        return new Cliente(id, nombre, apellido, email, telefono,
                nuevoEstado, createdAt, LocalDateTime.now());
    }
}
