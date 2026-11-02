package com.example.appmm_grupo5.viewmodel

import androidx.lifecycle.ViewModel
import com.example.appmm_grupo5.model.EncuestaUiState
import com.example.appmm_grupo5.model.PerfilUiState
import com.example.appmm_grupo5.validation.Validadores
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import android.net.Uri

// Guarda el estado del perfil del negocio y de la encuesta de intereses, ademas de aplicar validaciones.
class PerfilViewModel : ViewModel() {

    private val _perfil = MutableStateFlow(PerfilUiState())
    val perfil: StateFlow<PerfilUiState> = _perfil.asStateFlow()

    private val _encuesta = MutableStateFlow(EncuestaUiState())
    val encuesta: StateFlow<EncuestaUiState> = _encuesta.asStateFlow()

    private val _imagenUri = MutableStateFlow<Uri?>(null)
    val imagenUri: StateFlow<Uri?> = _imagenUri.asStateFlow()

    // Mientras el usuario escribe se muestra el error en vivo
    // Los errores de campos vacíos aparecen al presionar el boton
    private fun errorEnVivo(valor: String, error: String?): String? =
        if (valor.isEmpty()) null else error

    fun onNombreNegocioChange(valor: String) = _perfil.update {
        it.copy(
            nombreNegocio = valor,
            errores = it.errores.copy(nombreNegocio = errorEnVivo(valor, Validadores.nombreNegocio(valor)))
        )
    }

    fun onRutEmpresaChange(valor: String) = _perfil.update {
        it.copy(
            rutEmpresa = valor,
            errores = it.errores.copy(rutEmpresa = errorEnVivo(valor, Validadores.rut(valor)))
        )
    }

    // El negocio pasa a ser una lista
    fun onTipoNegocioChange(valor: String) = _perfil.update {
        it.copy(tipoNegocio = valor, errores = it.errores.copy(tipoNegocio = null))
    }

    fun onEmpleadosChange(valor: String) = _perfil.update {
        it.copy(
            empleados = valor,
            errores = it.errores.copy(empleados = errorEnVivo(valor, Validadores.empleados(valor)))
        )
    }

    fun onDireccionChange(valor: String) = _perfil.update {
        it.copy(
            direccion = valor,
            errores = it.errores.copy(direccion = errorEnVivo(valor, Validadores.direccion(valor)))
        )
    }

    // Validacion del perfil, true si todo esta bien
    fun validarPerfil(): Boolean {
        val errores = Validadores.validarPerfil(_perfil.value)
        _perfil.update { it.copy(errores = errores) }
        return !errores.hayErrores()
    }
    // Para marcar o borrar un interes
    fun onInteresChange(interes: String, marcado: Boolean) = _encuesta.update {
        val nuevos = if (marcado) it.intereses + interes else it.intereses - interes
        it.copy(
            intereses = nuevos,
            errores = it.errores.copy(intereses = if (nuevos.isEmpty()) it.errores.intereses else null)
        )
    }

    fun onObjetivoChange(valor: String) = _encuesta.update {
        it.copy(objetivo = valor, errores = it.errores.copy(objetivo = null))
    }

    fun onNovedadesChange(activo: Boolean) = _encuesta.update {
        it.copy(recibeNovedades = activo)
    }

    // Valida la encuesta. Devuelve true si no hay errores.
    fun validarEncuesta(): Boolean {
        val errores = Validadores.validarEncuesta(_encuesta.value)
        _encuesta.update { it.copy(errores = errores) }
        return !errores.hayErrores()
    }

    fun cargarPerfil(perfil: PerfilUiState) {
        _perfil.value = perfil
    }

    fun cargarEncuesta(encuesta: EncuestaUiState) {
        _encuesta.value = encuesta
    }

    fun reiniciar() {
        _perfil.value = PerfilUiState()
        _encuesta.value = EncuestaUiState()
        _imagenUri.value = null
    }

    // Al cancelar la foto de la galeria se conserva la foto anterior
    fun onImagenDeGaleria(uri: Uri?) {
        if (uri != null) _imagenUri.value = uri
    }

    // Si la camara tomo la foto se tira un true, si se cancela se conserva la foto anterior
    fun onImagenDeCamara(exito: Boolean, uri: Uri?) {
        if (exito && uri != null) _imagenUri.value = uri
    }


}