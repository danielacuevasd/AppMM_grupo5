package com.example.appmm_grupo5.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.appmm_grupo5.model.Contenido
import com.example.appmm_grupo5.model.Favorito
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritoDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun agregar(favorito: Favorito)

    @Query("DELETE FROM favoritos WHERE usuarioId = :usuarioId AND contenidoId = :contenidoId")
    suspend fun quitar(usuarioId: Long, contenidoId: Long)

    @Query("SELECT EXISTS(SELECT 1 FROM favoritos WHERE usuarioId = :usuarioId AND contenidoId = :contenidoId)")
    fun esFavorito(usuarioId: Long, contenidoId: Long): Flow<Boolean>

    // Ids de los favoritos, para marcar el corazón en la lista del catálogo
    @Query("SELECT contenidoId FROM favoritos WHERE usuarioId = :usuarioId")
    fun idsFavoritos(usuarioId: Long): Flow<List<Long>>

    // Los contenidos completos, para la pantalla "Mis guardados"
    @Query(
        """
        SELECT contenidos.* FROM contenidos
        INNER JOIN favoritos ON contenidos.id = favoritos.contenidoId
        WHERE favoritos.usuarioId = :usuarioId
        ORDER BY contenidos.titulo
        """
    )
    fun observarFavoritos(usuarioId: Long): Flow<List<Contenido>>
}