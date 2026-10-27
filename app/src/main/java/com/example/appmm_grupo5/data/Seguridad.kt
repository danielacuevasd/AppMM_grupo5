package com.example.appmm_grupo5.data

import java.security.MessageDigest

// Para no guardar claves en texto plano se guarda su hash SHA-256.
// En un producto real se usaría además una sal por usuario y un algoritmo
// pensado para claves (bcrypt, Argon2); para este MVP académico basta SHA-256.
object Seguridad {
    fun sha256(texto: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(texto.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}