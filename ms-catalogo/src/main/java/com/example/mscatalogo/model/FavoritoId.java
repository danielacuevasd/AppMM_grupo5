package com.example.mscatalogo.model;

import java.io.Serializable;
import java.util.Objects;

public class FavoritoId implements Serializable {

    private Long usuarioId;
    private Long contenidoId;

    public FavoritoId() {
    }

    public FavoritoId(Long usuarioId, Long contenidoId) {
        this.usuarioId = usuarioId;
        this.contenidoId = contenidoId;
    }

    public Long getUsuarioId() { return usuarioId; }
    public Long getContenidoId() { return contenidoId; }

    @Override
    public boolean equals(Object otro) {
        if (this == otro) return true;
        if (!(otro instanceof FavoritoId)) return false;
        FavoritoId that = (FavoritoId) otro;
        return Objects.equals(usuarioId, that.usuarioId) && Objects.equals(contenidoId, that.contenidoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(usuarioId, contenidoId);
    }
}
