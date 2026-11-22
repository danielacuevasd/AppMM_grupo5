package com.example.appmm_grupo5.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmm_grupo5.model.Contenido
import com.example.appmm_grupo5.network.RetrofitClient
import com.example.appmm_grupo5.repository.ContenidoRepository
import com.example.appmm_grupo5.repository.ResultadoCatalogo
import com.example.appmm_grupo5.repository.mensajeError
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CatalogoViewModel(
    private val repositorio: ContenidoRepository = ContenidoRepository(RetrofitClient.catalogoApi)
) : ViewModel() {

    private val _tipo = MutableStateFlow<String?>(null)
    val tipo: StateFlow<String?> = _tipo.asStateFlow()

    private val _categoria = MutableStateFlow<String?>(null)
    val categoria: StateFlow<String?> = _categoria.asStateFlow()

    private val _soloGuardados = MutableStateFlow(false)
    val soloGuardados: StateFlow<Boolean> = _soloGuardados.asStateFlow()

    private val _contenidos = MutableStateFlow<List<Contenido>>(emptyList())
    val contenidos: StateFlow<List<Contenido>> = _contenidos.asStateFlow()

    private val _cargando = MutableStateFlow(true)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var usuarioId: Long? = null
    private var trabajo: Job? = null

    init {
        recargar()
    }

    fun fijarUsuario(usuarioId: Long?) {
        if (this.usuarioId == usuarioId) return
        this.usuarioId = usuarioId
        if (_soloGuardados.value) recargar()
    }

    fun seleccionarTipo(tipo: String?) {
        _tipo.value = tipo
        recargar()
    }

    fun seleccionarCategoria(categoria: String?) {
        _categoria.value = categoria
        recargar()
    }

    fun alternarSoloGuardados() {
        _soloGuardados.value = !_soloGuardados.value
        recargar()
    }

    fun recargar() {
        trabajo?.cancel()
        trabajo = viewModelScope.launch {
            _cargando.value = true
            _error.value = null
            val resultado = consultar()
            if (resultado is ResultadoCatalogo.Exito) {
                _contenidos.value = resultado.datos
            } else {
                _contenidos.value = emptyList()
                _error.value = resultado.mensajeError()
            }
            _cargando.value = false
        }
    }

    private suspend fun consultar(): ResultadoCatalogo<List<Contenido>> {
        val tipo = _tipo.value
        val categoria = _categoria.value
        if (!_soloGuardados.value) return repositorio.filtrar(tipo, categoria)

        val id = usuarioId
        if (id == null || id <= 0L) return ResultadoCatalogo.Exito(emptyList())

        val resultado = repositorio.favoritos(id)
        if (resultado !is ResultadoCatalogo.Exito) return resultado
        return ResultadoCatalogo.Exito(
            resultado.datos.filter {
                (tipo == null || it.tipo == tipo) && (categoria == null || it.categoria == categoria)
            }
        )
    }
}
