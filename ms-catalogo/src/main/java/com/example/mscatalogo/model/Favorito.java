package com.example.mscatalogo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "favoritos")
@IdClass(FavoritoId.class)
public class Favorito {

    @Id
    @Column(name = "usuario_id")
    private Long usuarioId;

    @Id
    @Column(name = "contenido_id")
    private Long contenidoId;

    public Favorito() {
    }

    public Favorito(Long usuarioId, Long contenidoId) {
        this.usuarioId = usuarioId;
        this.contenidoId = contenidoId;
    }

    public Long getUsuarioId() { return usuarioId; }
    public Long getContenidoId() { return contenidoId; }
}
