package com.example.appmm_grupo5.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.appmm_grupo5.model.Usuario

@Dao
interface UsuarioDao {
    // Devuelve el id nuevo. Si el correo ya existe, lanza SQLiteConstraintException.
    @Insert
    suspend fun insertar(usuario: Usuario): Long

    @Query("SELECT * FROM usuarios WHERE correo = :correo LIMIT 1")
    suspend fun buscarPorCorreo(correo: String): Usuario?

    @Query("SELECT * FROM usuarios WHERE id = :id")
    suspend fun buscarPorId(id: Long): Usuario?
}