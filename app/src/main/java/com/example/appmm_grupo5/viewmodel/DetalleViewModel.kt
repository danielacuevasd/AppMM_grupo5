package com.example.appmm_grupo5.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmm_grupo5.model.Contenido
import com.example.appmm_grupo5.network.RetrofitClient
import com.example.appmm_grupo5.repository.ContenidoRepository
import com.example.appmm_grupo5.repository.ResultadoCatalogo
import com.example.appmm_grupo5.repository.mensajeError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetalleViewModel(
    private val repositorio: ContenidoRepository = ContenidoRepository(RetrofitClient.catalogoApi)
) : ViewModel() {

    private val _contenido = MutableStateFlow<Contenido?>(null)
    val contenido: StateFlow<Contenido?> = _contenido.asStateFlow()

    private val _esFavorito = MutableStateFlow(false)
    val esFavorito: StateFlow<Boolean> = _esFavorito.asStateFlow()

    private val _cargando = MutableStateFlow(true)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    private var contenidoId: Long? = null
    private var usuarioId: Long = 0L

    fun cargar(contenidoId: Long, usuarioId: Long) {
        this.contenidoId = contenidoId
        this.usuarioId = usuarioId
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null
            val resultado = repositorio.buscarPorId(contenidoId)
            if (resultado is ResultadoCatalogo.Exito) {
                _contenido.value = resultado.datos
                _esFavorito.value = usuarioId > 0L && estaGuardado(usuarioId, contenidoId)
            } else {
                _contenido.value = null
                _error.value = resultado.mensajeError()
            }
            _cargando.value = false
        }
    }

    fun cambiarFavorito() {
        val id = contenidoId ?: return
        if (usuarioId <= 0L) return
        viewModelScope.launch {
            val marcar = !_esFavorito.value
            val resultado = repositorio.cambiarFavorito(usuarioId, id, marcar)
            if (resultado is ResultadoCatalogo.Exito) {
                _esFavorito.value = marcar
                _mensaje.value = if (marcar) "Guardado en tus favoritos" else "Quitado de tus favoritos"
            } else {
                _mensaje.value = resultado.mensajeError()
            }
        }
    }

    fun mensajeMostrado() {
        _mensaje.value = null
    }

    private suspend fun estaGuardado(usuarioId: Long, contenidoId: Long): Boolean {
        val resultado = repositorio.favoritos(usuarioId)
        return resultado is ResultadoCatalogo.Exito && resultado.datos.any { it.id == contenidoId }
    }
}
