package com.example.appmm_grupo5.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmm_grupo5.model.Postre
import com.example.appmm_grupo5.network.MealRetrofit
import com.example.appmm_grupo5.repository.MealRepository
import com.example.appmm_grupo5.repository.ResultadoApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PostresUiState(
    val cargando: Boolean = false,
    val postres: List<Postre> = emptyList(),
    val busqueda: String = "",
    val error: String? = null,
    val detalle: Postre? = null,
    val cargandoDetalle: Boolean = false,
    val errorDetalle: String? = null
) {
    // Lista filtrada por lo que se escribe en el buscador
    val visibles: List<Postre>
        get() = if (busqueda.isBlank()) postres
        else postres.filter { it.nombre.contains(busqueda.trim(), ignoreCase = true) }
}

// ViewModel de la pantalla "Ideas de postres"
class PostresViewModel(
    private val repositorio: MealRepository = MealRepository(MealRetrofit.api)
) : ViewModel() {

    private val _estado = MutableStateFlow(PostresUiState())
    val estado: StateFlow<PostresUiState> = _estado

    init {
        cargarPostres()
    }

    fun cargarPostres() {
        viewModelScope.launch {
            _estado.update { it.copy(cargando = true, error = null) }
            when (val resultado = repositorio.obtenerPostres()) {
                is ResultadoApi.Exito ->
                    _estado.update { it.copy(cargando = false, postres = resultado.datos) }
                ResultadoApi.ErrorRed ->
                    _estado.update { it.copy(cargando = false, error = "No hay conexión a internet. Intenta de nuevo.") }
                else ->
                    _estado.update { it.copy(cargando = false, error = "El servicio de postres no respondió correctamente.") }
            }
        }
    }

    fun onBusquedaChange(texto: String) = _estado.update { it.copy(busqueda = texto) }

    // Muestra el postre al tiro (con lo que ya tenemos) y pide el detalle a la API
    fun abrirDetalle(postre: Postre) {
        _estado.update { it.copy(detalle = postre, cargandoDetalle = true, errorDetalle = null) }
        viewModelScope.launch {
            when (val resultado = repositorio.obtenerDetalle(postre.id)) {
                is ResultadoApi.Exito ->
                    _estado.update { it.copy(detalle = resultado.datos, cargandoDetalle = false) }
                ResultadoApi.ErrorRed ->
                    _estado.update { it.copy(cargandoDetalle = false, errorDetalle = "No hay conexión a internet.") }
                else ->
                    _estado.update { it.copy(cargandoDetalle = false, errorDetalle = "No se pudo cargar el detalle.") }
            }
        }
    }

    fun cerrarDetalle() = _estado.update { it.copy(detalle = null, errorDetalle = null) }
}