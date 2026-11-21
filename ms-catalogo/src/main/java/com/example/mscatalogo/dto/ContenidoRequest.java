package com.example.mscatalogo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ContenidoRequest(
        @NotBlank String tipo,
        @NotBlank String titulo,
        @NotBlank String categoria,
        @NotBlank String descripcion,
        @Min(1) int duracionMinutos,
        @NotBlank String nivel) {
}
