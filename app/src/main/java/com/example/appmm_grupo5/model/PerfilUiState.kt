package com.example.appmm_grupo5.model

// Esto los usan la pantalla y las validaciones
object OpcionesPerfil {
    val tiposNegocio = listOf(
        "Pastelería", "Panadería", "Heladería", "Chocolatería", "Cafetería o restaurante", "Otro"
    )
    val tamanosNegocio = listOf(
        "Micro (1 a 5 personas)", "Pequeño (6 a 20 personas)", "Mediano (más de 20 personas)"
    )
    val intereses = listOf(
        "Chocolatería", "Pastelería", "Heladería", "Bollería", "Masas de hoja", "Galletas", "Empanadas", "Horneo"
    )
    val objetivos = listOf(
        "Mejorar mis recetas", "Ampliar mi carta", "Reducir costos", "Aprender técnicas nuevas"
    )

    // El tamaño no lo elige el usuario: se calcula según la cantidad de personas
    fun tamanoSegun(personas: Int): String = when {
        personas <= 5 -> tamanosNegocio[0]
        personas <= 20 -> tamanosNegocio[1]
        else -> tamanosNegocio[2]
    }
}

//null significa que el campo está bien
data class PerfilErrores(
    val nombreNegocio: String? = null,
    val rutEmpresa: String? = null,
    val tipoNegocio: String? = null,
    val empleados: String? = null,
    val direccion: String? = null
) {
    fun mensajes(): List<String> =
        listOfNotNull(nombreNegocio, rutEmpresa, tipoNegocio, empleados, direccion)

    fun hayErrores(): Boolean = mensajes().isNotEmpty()
}

// Lo que escribe el usuario en el perfil de su negocio y los errores que se le muestran
data class PerfilUiState(
    val nombreNegocio: String = "",
    val rutEmpresa: String = "",
    val tipoNegocio: String = "",
    val empleados: String = "",
    val direccion: String = "",
    val errores: PerfilErrores = PerfilErrores()
) {
    // Se deduce de la cantidad de personas; queda vacío mientras no haya un número válido
    val tamanoNegocio: String
        get() = empleados.trim().toIntOrNull()?.let { OpcionesPerfil.tamanoSegun(it) } ?: ""
}

data class EncuestaErrores(
    val intereses: String? = null,
    val objetivo: String? = null
) {
    fun mensajes(): List<String> = listOfNotNull(intereses, objetivo)

    fun hayErrores(): Boolean = mensajes().isNotEmpty()
}

data class EncuestaUiState(
    val intereses: Set<String> = emptySet(),
    val objetivo: String = "",
    val recibeNovedades: Boolean = true,
    val errores: EncuestaErrores = EncuestaErrores()
)