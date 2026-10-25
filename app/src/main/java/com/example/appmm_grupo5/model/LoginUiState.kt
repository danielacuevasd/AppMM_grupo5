package com.example.appmm_grupo5.model

// Estado del formulario de ingreso: lo que escribe el usuario y sus errores
data class LoginUiState(
    val correo: String = "",
    val clave: String = "",
    val errores: FormularioErrores = FormularioErrores()
)