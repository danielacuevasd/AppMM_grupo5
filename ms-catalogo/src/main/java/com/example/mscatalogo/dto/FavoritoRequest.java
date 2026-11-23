package com.example.mscatalogo.dto;

import jakarta.validation.constraints.NotNull;

public record FavoritoRequest(
        @NotNull Long usuarioId,
        @NotNull Long contenidoId) {
}
