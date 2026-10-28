package com.example.appmm_grupo5.repository

import com.example.appmm_grupo5.data.Seguridad
import com.example.appmm_grupo5.data.UsuarioDao
import com.example.appmm_grupo5.model.Usuario

// Repositorio de usuarios, guarda y recupera usuarios de la base de datos
class UsuarioRepository(private val usuarioDao: UsuarioDao) {

    // Devuelve el usuario creado, o null si ese correo ya estaba registrado.
    // La clave por seguridad se guarda como un hash SHA-256
    suspend fun registrar(nombre: String, correo: String, telefono: String, clave: String): Usuario? {
        val correoLimpio = normalizarCorreo(correo)
        if (usuarioDao.buscarPorCorreo(correoLimpio) != null) return null

        val nuevo = Usuario(
            nombre = nombre.trim(),
            correo = correoLimpio,
            telefono = telefono.trim(),
            claveHash = Seguridad.sha256(clave)
        )
        val id = usuarioDao.insertar(nuevo)
        return nuevo.copy(id = id)
    }

    // Si la clave y correo coinciden con un usuario, devuelve el usuario.
    suspend fun iniciarSesion(correo: String, clave: String): Usuario? {
        val usuario = usuarioDao.buscarPorCorreo(normalizarCorreo(correo)) ?: return null
        return if (usuario.claveHash == Seguridad.sha256(clave)) usuario else null
    }

    suspend fun buscarPorCorreo(correo: String): Usuario? =
        usuarioDao.buscarPorCorreo(normalizarCorreo(correo))

    suspend fun buscarPorId(id: Long): Usuario? = usuarioDao.buscarPorId(id)
    private fun normalizarCorreo(correo: String): String = correo.trim().lowercase()
}