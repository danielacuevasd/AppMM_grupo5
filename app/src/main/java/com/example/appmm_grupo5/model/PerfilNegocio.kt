package com.example.appmm_grupo5.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "perfiles_negocio",
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["usuarioId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PerfilNegocio(
    @PrimaryKey
    val usuarioId: Long,
    val nombreNegocio: String,
    val rutEmpresa: String,
    val tipoNegocio: String,
    val empleados: Int,
    val direccion: String
)