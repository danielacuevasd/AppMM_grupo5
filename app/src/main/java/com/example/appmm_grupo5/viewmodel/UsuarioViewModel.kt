package com.example.appmm_grupo5.viewmodel

import androidx.lifecycle.ViewModel
import com.example.appmm_grupo5.model.FormularioUiState
import com.example.appmm_grupo5.model.LoginUiState
import com.example.appmm_grupo5.validation.Validadores
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// Guarda el estado de los formularios de registro e ingreso.
// Las reglas viven en Validadores; aquí solo se decide cuándo aplicarlas.
class UsuarioViewModel : ViewModel() {

    private val _registro = MutableStateFlow(FormularioUiState())
    val registro: StateFlow<FormularioUiState> = _registro.asStateFlow()

    private val _login = MutableStateFlow(LoginUiState())
    val login: StateFlow<LoginUiState> = _login.asStateFlow()

    // Mientras el usuario escribe se muestra el error, pero un campo vacío no se marca todavía.
    // Los errores de campos vacíos aparecen al presionar el botón.
    private fun errorEnVivo(valor: String, error: String?): String? =
        if (valor.isEmpty()) null else error

    // ---------- Registro ----------

    fun onNombreChange(valor: String) = _registro.update {
        it.copy(nombre = valor, errores = it.errores.copy(nombre = errorEnVivo(valor, Validadores.nombre(valor))))
    }

    fun onCorreoChange(valor: String) = _registro.update {
        it.copy(correo = valor, errores = it.errores.copy(correo = errorEnVivo(valor, Validadores.correo(valor))))
    }

    fun onTelefonoChange(valor: String) = _registro.update {
        it.copy(telefono = valor, errores = it.errores.copy(telefono = errorEnVivo(valor, Validadores.telefono(valor))))
    }

    // Al cambiar la clave también se vuelve a revisar la confirmación
    fun onClaveChange(valor: String) = _registro.update {
        it.copy(
            clave = valor,
            errores = it.errores.copy(
                clave = errorEnVivo(valor, Validadores.clave(valor)),
                confirmarClave = errorEnVivo(it.confirmarClave, Validadores.confirmarClave(valor, it.confirmarClave))
            )
        )
    }

    fun onConfirmarClaveChange(valor: String) = _registro.update {
        it.copy(
            confirmarClave = valor,
            errores = it.errores.copy(confirmarClave = errorEnVivo(valor, Validadores.confirmarClave(it.clave, valor)))
        )
    }

    fun onTerminosChange(marcado: Boolean) = _registro.update {
        it.copy(
            aceptaTerminos = marcado,
            errores = it.errores.copy(aceptaTerminos = if (marcado) null else it.errores.aceptaTerminos)
        )
    }

    // Valida todo el formulario. Devuelve true si no hay errores.
    fun validarRegistro(): Boolean {
        val errores = Validadores.validarRegistro(_registro.value)
        _registro.update { it.copy(errores = errores) }
        return !errores.hayErrores()
    }

    // ---------- Login ----------

    fun onLoginCorreoChange(valor: String) = _login.update {
        it.copy(correo = valor, errores = it.errores.copy(correo = errorEnVivo(valor, Validadores.correo(valor))))
    }

    fun onLoginClaveChange(valor: String) = _login.update {
        it.copy(clave = valor, errores = it.errores.copy(clave = null))
    }

    fun validarLogin(): Boolean {
        val estado = _login.value
        val errores = Validadores.validarLogin(estado.correo, estado.clave)
        _login.update { it.copy(errores = errores) }
        return !errores.hayErrores()
    }
}