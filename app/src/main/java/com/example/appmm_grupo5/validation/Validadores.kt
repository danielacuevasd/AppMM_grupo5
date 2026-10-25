package com.example.appmm_grupo5.validation

import com.example.appmm_grupo5.model.FormularioErrores
import com.example.appmm_grupo5.model.FormularioUiState

// Reglas de validación. Cada función devuelve null si el dato es válido, o el mensaje de error que debe mostrar la pantalla.
// Aquí no hay nada de interfaz: así se pueden probar sin emulador.
object Validadores {

    fun obligatorio(valor: String, mensaje: String): String? =
        if (valor.isBlank()) mensaje else null

    fun nombre(valor: String): String? {
        val limpio = valor.trim()
        if (limpio.isEmpty()) return "El nombre es obligatorio"
        if (limpio.length < 3) return "El nombre debe tener al menos 3 letras"
        if (limpio.length > 50) return "El nombre no puede superar los 50 caracteres"
        if (!limpio.all { it.isLetter() || it == ' ' }) return "El nombre solo puede tener letras y espacios"
        return null
    }

    fun correo(valor: String): String? {
        val limpio = valor.trim()
        if (limpio.isEmpty()) return "El correo es obligatorio"
        val partes = limpio.split("@")
        val valido = partes.size == 2 &&
                partes[0].isNotEmpty() &&
                !limpio.contains(" ") &&
                partes[1].contains(".") &&
                !partes[1].startsWith(".") &&
                !partes[1].endsWith(".")
        return if (valido) null else "Ingresa un correo válido, por ejemplo nombre@correo.cl"
    }

    // Celular chileno: 9 dígitos que parten con 9, con o sin +56
    fun telefono(valor: String): String? {
        if (valor.isBlank()) return "El teléfono es obligatorio"
        var numero = valor.replace(" ", "").replace("-", "")
        if (numero.startsWith("+56")) numero = numero.removePrefix("+56")
        if (!numero.all { it.isDigit() }) return "El teléfono solo debe tener números"
        if (numero.length != 9 || !numero.startsWith("9")) {
            return "Ingresa un celular de 9 dígitos que parta con 9"
        }
        return null
    }

    // RUT chileno con dígito verificador (módulo 11), con o sin puntos y guion
    fun rut(valor: String): String? {
        if (valor.isBlank()) return "El RUT es obligatorio"
        val limpio = valor.replace(".", "").replace("-", "").uppercase()
        if (limpio.length !in 8..9) return "Ingresa un RUT válido, por ejemplo 12.345.678-5"
        val cuerpo = limpio.dropLast(1)
        val digito = limpio.last()
        if (!cuerpo.all { it.isDigit() }) return "El RUT solo puede tener números y el dígito verificador"

        var suma = 0
        var factor = 2
        for (i in cuerpo.indices.reversed()) {
            suma += cuerpo[i].digitToInt() * factor
            factor = if (factor == 7) 2 else factor + 1
        }
        val resto = 11 - (suma % 11)
        val esperado = when (resto) {
            11 -> '0'
            10 -> 'K'
            else -> resto.digitToChar()
        }
        return if (digito == esperado) null else "El dígito verificador del RUT no es correcto"
    }

    fun clave(valor: String): String? {
        if (valor.isBlank()) return "La clave es obligatoria"
        if (valor.length < 8) return "La clave debe tener al menos 8 caracteres"
        if (!valor.any { it.isLetter() } || !valor.any { it.isDigit() }) {
            return "Usa letras y números en la clave"
        }
        return null
    }

    // Dependencia entre campos: la confirmación se compara con la clave
    fun confirmarClave(clave: String, confirmacion: String): String? {
        if (confirmacion.isBlank()) return "Confirma tu clave"
        return if (clave == confirmacion) null else "Las claves no coinciden"
    }

    fun terminos(aceptado: Boolean): String? =
        if (aceptado) null else "Debes aceptar los términos y la política de privacidad"

    // Para campos numéricos con límites, por ejemplo la cantidad de personas
    fun rango(valor: String, minimo: Int, maximo: Int, campo: String): String? {
        val numero = valor.trim().toIntOrNull() ?: return "$campo debe ser un número"
        return if (numero in minimo..maximo) null else "$campo debe estar entre $minimo y $maximo"
    }

    // Valida todo el formulario de registro de una vez
    fun validarRegistro(estado: FormularioUiState): FormularioErrores = FormularioErrores(
        nombre = nombre(estado.nombre),
        correo = correo(estado.correo),
        telefono = telefono(estado.telefono),
        clave = clave(estado.clave),
        confirmarClave = confirmarClave(estado.clave, estado.confirmarClave),
        aceptaTerminos = terminos(estado.aceptaTerminos)
    )

    // En el login solo se exige correo válido y clave escrita
    fun validarLogin(correo: String, clave: String): FormularioErrores = FormularioErrores(
        correo = correo(correo),
        clave = obligatorio(clave, "Ingresa tu clave")
    )
}