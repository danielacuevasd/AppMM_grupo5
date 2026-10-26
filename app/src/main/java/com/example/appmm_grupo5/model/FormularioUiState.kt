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
    // Lista de los mensajes de error que hay en este momento
    fun mensajes(): List<String> =
        listOfNotNull(nombre, correo, telefono, clave, confirmarClave, aceptaTerminos)

    fun hayErrores(): Boolean = mensajes().isNotEmpty()
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