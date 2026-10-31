package com.example.appmm_grupo5.viewmodel

import android.app.Application
import android.database.sqlite.SQLiteConstraintException
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
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Estado de la pantalla de detalle: el contenido abierto y si está en favoritos del usuario.
class DetalleViewModel(application: Application) : AndroidViewModel(application) {

    private val baseDeDatos = AppDatabase.obtener(application)
    private val repositorio = ContenidoRepository(baseDeDatos.contenidoDao(), baseDeDatos.favoritoDao())

    private val _contenido = MutableStateFlow<Contenido?>(null)
    val contenido: StateFlow<Contenido?> = _contenido.asStateFlow()

    // Par (usuarioId, contenidoId) para consultar y cambiar el favorito
    private val _claveFavorito = MutableStateFlow<Pair<Long, Long>?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val esFavorito: StateFlow<Boolean> = _claveFavorito
        .flatMapLatest { clave ->
            if (clave == null) flowOf(false) else repositorio.esFavorito(clave.first, clave.second)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun cargar(contenidoId: Long, usuarioId: Long) {
        // Sin una sesión válida no se pueden guardar favoritos
        _claveFavorito.value = if (usuarioId > 0L) usuarioId to contenidoId else null
        viewModelScope.launch { _contenido.value = repositorio.buscarPorId(contenidoId) }
    }

    fun cambiarFavorito() {
        val clave = _claveFavorito.value ?: return
        viewModelScope.launch {
            try {
                repositorio.cambiarFavorito(clave.first, clave.second, !esFavorito.value)
            } catch (e: SQLiteConstraintException) {
                // La sesión guardada apunta a un usuario que ya no existe en la base
                // (por ejemplo, si se recreó). Se ignora; al volver a ingresar se resuelve.
            }
        }
    }
}