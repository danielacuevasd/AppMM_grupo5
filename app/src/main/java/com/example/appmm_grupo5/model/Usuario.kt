package com.example.appmm_grupo5.model

// Usuario que viene de ms-usuarios (ya no se guarda en Room)
data class Usuario(
    val id: Long,
    val nombre: String,
    val correo: String,
    val telefono: String
)