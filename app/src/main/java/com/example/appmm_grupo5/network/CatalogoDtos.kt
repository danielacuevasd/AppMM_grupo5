package com.example.appmm_grupo5.network

data class ContenidoRequest(
    val tipo: String,
    val titulo: String,
    val categoria: String,
    val descripcion: String,
    val duracionMinutos: Int,
    val nivel: String
)

data class ContenidoResponse(
    val id: Long,
    val tipo: String,
    val titulo: String,
    val categoria: String,
    val descripcion: String,
    val duracionMinutos: Int,
    val nivel: String
)

data class FavoritoRequest(
    val usuarioId: Long,
    val contenidoId: Long
)

data class FavoritoResponse(
    val usuarioId: Long,
    val contenidoId: Long
)
