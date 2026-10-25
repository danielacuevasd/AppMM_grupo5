package com.example.appmm_grupo5.model

// Errores de cada campo: null significa que el campo está bien
data class FormularioErrores(
    val nombre: String? = null,
    val correo: String? = null,
    val telefono: String? = null,
    val clave: String? = null,
    val confirmarClave: String? = null,
    val aceptaTerminos: String? = null
) {
    fun hayErrores(): Boolean =
        listOf(nombre, correo, telefono, clave, confirmarClave, aceptaTerminos).any { it != null }
}

// Estado completo del formulario de registro: lo que escribe el usuario y sus errores
data class FormularioUiState(
    val nombre: String = "",
    val correo: String = "",
    val telefono: String = "",
    val clave: String = "",
    val confirmarClave: String = "",
    val aceptaTerminos: Boolean = false,
    val errores: FormularioErrores = FormularioErrores()
)