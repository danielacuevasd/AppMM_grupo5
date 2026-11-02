package com.example.appmm_grupo5.repository

import com.example.appmm_grupo5.data.ContenidoDao
import com.example.appmm_grupo5.data.FavoritoDao
import com.example.appmm_grupo5.model.Contenido
import com.example.appmm_grupo5.model.Favorito
import kotlinx.coroutines.flow.Flow

// Repositorio del catalogo
class ContenidoRepository(
    private val contenidoDao: ContenidoDao,
    private val favoritoDao: FavoritoDao
) {

    // Lista del catalogo, con tipo o categoria.
    // Devuelve un Flow: si el contenido cambia, la pantalla se actualiza sola.
    // Null para cuando no hay filtros aplicados
    fun filtrar(tipo: String? = null, categoria: String? = null): Flow<List<Contenido>> =
        contenidoDao.filtrar(tipo, categoria)

    suspend fun buscarPorId(id: Long): Contenido? = contenidoDao.buscarPorId(id)

    // Los favoritos
    fun idsFavoritos(usuarioId: Long): Flow<List<Long>> = favoritoDao.idsFavoritos(usuarioId)

    fun esFavorito(usuarioId: Long, contenidoId: Long): Flow<Boolean> =
        favoritoDao.esFavorito(usuarioId, contenidoId)

    // Contenido en guardados
    fun observarFavoritos(usuarioId: Long): Flow<List<Contenido>> =
        favoritoDao.observarFavoritos(usuarioId)

    // Para agregar o quitar de favoritos
    suspend fun cambiarFavorito(usuarioId: Long, contenidoId: Long, marcar: Boolean) {
        if (marcar) {
            favoritoDao.agregar(Favorito(usuarioId, contenidoId))
        } else {
            favoritoDao.quitar(usuarioId, contenidoId)
        }
    }
}