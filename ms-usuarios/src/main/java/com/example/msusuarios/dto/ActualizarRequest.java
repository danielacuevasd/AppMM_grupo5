package com.example.msusuarios.dto;

import jakarta.validation.constraints.NotBlank;

// La clave es opcional: si viene vacía o null, no se cambia
public record ActualizarRequest(
        @NotBlank String nombre,
        @NotBlank String telefono,
        String clave) {
}