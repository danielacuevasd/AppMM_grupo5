package com.example.appmm_grupo5.model

// Tipos de contenido que muestra el catálogo
object TipoContenido {
    const val RECETA = "Receta"
    const val CURSO = "Curso"
    const val VIDEO = "Video"
    val todos = listOf(RECETA, CURSO, VIDEO)
}

data class Contenido(
    val id: Long = 0,
    val tipo: String,
    val titulo: String,
    val categoria: String,          // misma lista que los intereses de la encuesta
    val descripcion: String,
    val duracionMinutos: Int,
    val nivel: String               // Básico, Intermedio o Avanzado
)