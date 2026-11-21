package com.example.appmm_grupo5.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "encuestas",
)
data class Encuesta(
    @PrimaryKey
    val usuarioId: Long,
    val intereses: String,          // por ejemplo: "Chocolatería,Galletas"
    val objetivo: String,
    val recibeNovedades: Boolean
) {
    fun listaIntereses(): Set<String> =
        if (intereses.isBlank()) emptySet() else intereses.split(",").toSet()

    companion object {
        fun unirIntereses(intereses: Set<String>): String = intereses.joinToString(",")
    }
}