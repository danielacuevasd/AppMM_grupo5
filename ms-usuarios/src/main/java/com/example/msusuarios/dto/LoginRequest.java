package com.example.msusuarios.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank String correo,
        @NotBlank String clave) {
}