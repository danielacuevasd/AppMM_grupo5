package com.example.mscatalogo.dto;

public record ContenidoResponse(
        Long id,
        String tipo,
        String titulo,
        String categoria,
        String descripcion,
        int duracionMinutos,
        String nivel) {
}
