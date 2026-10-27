package com.example.appmm_grupo5.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.appmm_grupo5.model.Encuesta
import kotlinx.coroutines.flow.Flow

@Dao
interface EncuestaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(encuesta: Encuesta)

    @Query("SELECT * FROM encuestas WHERE usuarioId = :usuarioId")
    suspend fun obtener(usuarioId: Long): Encuesta?

    @Query("SELECT * FROM encuestas WHERE usuarioId = :usuarioId")
    fun observar(usuarioId: Long): Flow<Encuesta?>
}