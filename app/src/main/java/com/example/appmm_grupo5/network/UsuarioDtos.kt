package com.example.appmm_grupo5.network

// Lo que se envía y se recibe del microservicio (mismos nombres que el JSON)
data class RegistroRequest(
    val nombre: String,
    val correo: String,
    val telefono: String,
    val clave: String
)

data class LoginRequest(val correo: String, val clave: String)

data class ActualizarRequest(
    val nombre: String,
    val telefono: String,
    val clave: String? = null // si es null, no se cambia la clave
)

// El servidor nunca devuelve la clave
data class UsuarioResponse(
    val id: Long,
    val nombre: String,
    val correo: String,
    val telefono: String
)