package com.example.appmm_grupo5.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.appmm_grupo5.model.PerfilNegocio
import kotlinx.coroutines.flow.Flow

@Dao
interface PerfilNegocioDao {
    // Si el usuario ya tenía perfil, lo reemplaza
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(perfil: PerfilNegocio)

    @Query("SELECT * FROM perfiles_negocio WHERE usuarioId = :usuarioId")
    suspend fun obtener(usuarioId: Long): PerfilNegocio?

    @Query("SELECT * FROM perfiles_negocio WHERE usuarioId = :usuarioId")
    fun observar(usuarioId: Long): Flow<PerfilNegocio?>
}