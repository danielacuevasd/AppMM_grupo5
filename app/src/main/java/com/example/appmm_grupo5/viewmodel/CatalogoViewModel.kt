package com.example.appmm_grupo5.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmm_grupo5.data.AppDatabase
import com.example.appmm_grupo5.model.Contenido
import com.example.appmm_grupo5.repository.ContenidoRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Estado del catálogo: qué filtros hay elegidos y qué contenidos se muestran.
// null en un filtro significa "sin filtro".
class CatalogoViewModel(application: Application) : AndroidViewModel(application) {

    private val baseDeDatos = AppDatabase.obtener(application)
    private val repositorio = ContenidoRepository(baseDeDatos.contenidoDao(), baseDeDatos.favoritoDao())

    private val _tipo = MutableStateFlow<String?>(null)
    val tipo: StateFlow<String?> = _tipo.asStateFlow()

    private val _categoria = MutableStateFlow<String?>(null)
    val categoria: StateFlow<String?> = _categoria.asStateFlow()

    // Cada vez que cambia un filtro se vuelve a consultar la base.
    // Como Room entrega un Flow, la lista también se actualiza si cambian los datos.
    @OptIn(ExperimentalCoroutinesApi::class)
    val contenidos: StateFlow<List<Contenido>> =
        combine(_tipo, _categoria) { tipo, categoria -> tipo to categoria }
            .flatMapLatest { (tipo, categoria) -> repositorio.filtrar(tipo, categoria) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // La primera vez la tabla está vacía: se carga el contenido de ejemplo
        viewModelScope.launch { baseDeDatos.cargarDatosEjemploSiHaceFalta() }
    }

    fun seleccionarTipo(tipo: String?) {
        _tipo.value = tipo
    }

    fun seleccionarCategoria(categoria: String?) {
        _categoria.value = categoria
    }
}