package com.example.appmm_grupo5.repository

import com.example.appmm_grupo5.model.Usuario
import com.example.appmm_grupo5.network.LoginRequest
import com.example.appmm_grupo5.network.RegistroRequest
import com.example.appmm_grupo5.network.UsuarioApi
import com.example.appmm_grupo5.network.UsuarioResponse
import retrofit2.Response
import java.io.IOException

// Resultado de cada operación, para que la pantalla sepa qué mensaje mostrar
sealed class ResultadoUsuario {
    data class Exito(val usuario: Usuario) : ResultadoUsuario()
    object CorreoRepetido : ResultadoUsuario()        // 409
    object CredencialesIncorrectas : ResultadoUsuario() // 401
    object NoEncontrado : ResultadoUsuario()          // 404
    object ErrorRed : ResultadoUsuario()              // sin conexión o servidor apagado
    object ErrorServidor : ResultadoUsuario()         // cualquier otro código
}

// Repositorio de usuarios: ahora habla con el microservicio ms-usuarios
class UsuarioRepository(private val api: UsuarioApi) {

    suspend fun registrar(nombre: String, correo: String, telefono: String, clave: String): ResultadoUsuario =
        llamar {
            api.registrar(RegistroRequest(nombre.trim(), normalizarCorreo(correo), telefono.trim(), clave))
        }

    suspend fun iniciarSesion(correo: String, clave: String): ResultadoUsuario =
        llamar { api.login(LoginRequest(normalizarCorreo(correo), clave)) }

    suspend fun buscarPorId(id: Long): ResultadoUsuario = llamar { api.obtener(id) }

    // Ejecuta la llamada y traduce el código HTTP a un resultado
    private suspend fun llamar(bloque: suspend () -> Response<UsuarioResponse>): ResultadoUsuario =
        try {
            val respuesta = bloque()
            when {
                respuesta.isSuccessful ->
                    respuesta.body()?.let { ResultadoUsuario.Exito(it.aUsuario()) }
                        ?: ResultadoUsuario.ErrorServidor
                respuesta.code() == 409 -> ResultadoUsuario.CorreoRepetido
                respuesta.code() == 401 -> ResultadoUsuario.CredencialesIncorrectas
                respuesta.code() == 404 -> ResultadoUsuario.NoEncontrado
                else -> ResultadoUsuario.ErrorServidor
            }
        } catch (e: IOException) {
            ResultadoUsuario.ErrorRed
        }

    private fun UsuarioResponse.aUsuario() = Usuario(id, nombre, correo, telefono)

    private fun normalizarCorreo(correo: String): String = correo.trim().lowercase()
}