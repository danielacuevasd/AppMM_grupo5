package com.example.appmm_grupo5.repository

import com.example.appmm_grupo5.model.Contenido
import com.example.appmm_grupo5.network.CatalogoApi
import com.example.appmm_grupo5.network.ContenidoResponse
import com.example.appmm_grupo5.network.FavoritoRequest
import retrofit2.Response
import java.io.IOException

sealed class ResultadoCatalogo<out T> {
    data class Exito<out T>(val datos: T) : ResultadoCatalogo<T>()
    object NoEncontrado : ResultadoCatalogo<Nothing>()
    object ErrorRed : ResultadoCatalogo<Nothing>()
    object ErrorServidor : ResultadoCatalogo<Nothing>()
}

fun ResultadoCatalogo<*>.mensajeError(): String? = when (this) {
    is ResultadoCatalogo.Exito -> null
    ResultadoCatalogo.NoEncontrado -> "No se encontró el contenido"
    ResultadoCatalogo.ErrorRed -> "No hay conexión con el servidor. Revisa que ms-catalogo esté encendido"
    ResultadoCatalogo.ErrorServidor -> "El servidor tuvo un problema. Intenta de nuevo"
}

class ContenidoRepository(private val api: CatalogoApi) {

    suspend fun filtrar(tipo: String? = null, categoria: String? = null): ResultadoCatalogo<List<Contenido>> =
        llamar({ api.listar(tipo, categoria) }) { lista -> lista.map { it.aContenido() } }

    suspend fun buscarPorId(id: Long): ResultadoCatalogo<Contenido> =
        llamar({ api.obtener(id) }) { it.aContenido() }

    suspend fun favoritos(usuarioId: Long): ResultadoCatalogo<List<Contenido>> =
        llamar({ api.favoritosDeUsuario(usuarioId) }) { lista -> lista.map { it.aContenido() } }

    suspend fun cambiarFavorito(usuarioId: Long, contenidoId: Long, marcar: Boolean): ResultadoCatalogo<Unit> =
        if (marcar) {
            llamar({ api.agregarFavorito(FavoritoRequest(usuarioId, contenidoId)) }) { }
        } else {
            llamarSinCuerpo { api.quitarFavorito(usuarioId, contenidoId) }
        }

    private suspend fun <R, T> llamar(
        bloque: suspend () -> Response<R>,
        convertir: (R) -> T
    ): ResultadoCatalogo<T> =
        try {
            val respuesta = bloque()
            val cuerpo = respuesta.body()
            when {
                respuesta.isSuccessful && cuerpo != null -> ResultadoCatalogo.Exito(convertir(cuerpo))
                respuesta.code() == 404 -> ResultadoCatalogo.NoEncontrado
                else -> ResultadoCatalogo.ErrorServidor
            }
        } catch (e: IOException) {
            ResultadoCatalogo.ErrorRed
        }

    private suspend fun llamarSinCuerpo(bloque: suspend () -> Response<Unit>): ResultadoCatalogo<Unit> =
        try {
            val respuesta = bloque()
            when {
                respuesta.isSuccessful -> ResultadoCatalogo.Exito(Unit)
                respuesta.code() == 404 -> ResultadoCatalogo.Exito(Unit)
                else -> ResultadoCatalogo.ErrorServidor
            }
        } catch (e: IOException) {
            ResultadoCatalogo.ErrorRed
        }

    private fun ContenidoResponse.aContenido() =
        Contenido(id, tipo, titulo, categoria, descripcion, duracionMinutos, nivel)
}
