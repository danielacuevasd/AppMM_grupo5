package com.example.appmm_grupo5.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.appmm_grupo5.model.Contenido
import kotlinx.coroutines.flow.Flow

@Dao
interface ContenidoDao {
    @Insert
    suspend fun insertarTodos(contenidos: List<Contenido>)

    @Query("SELECT COUNT(*) FROM contenidos")
    suspend fun contar(): Int

    @Query(
        """
        SELECT * FROM contenidos
        WHERE (:tipo IS NULL OR tipo = :tipo)
          AND (:categoria IS NULL OR categoria = :categoria)
        ORDER BY titulo
        """
    )
    fun filtrar(tipo: String?, categoria: String?): Flow<List<Contenido>>

    @Query("SELECT * FROM contenidos WHERE id = :id")
    suspend fun buscarPorId(id: Long): Contenido?
}