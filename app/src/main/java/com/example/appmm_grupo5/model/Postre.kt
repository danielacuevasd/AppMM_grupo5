package com.example.appmm_grupo5.model

// Postre que mostramos en la app (viene de TheMealDB)
data class Postre(
    val id: String,
    val nombre: String,
    val imagen: String?,
    val categoria: String? = null,
    val area: String? = null,
    val instrucciones: String? = null
)