package com.example.appmm_grupo5.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmm_grupo5.data.SesionDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// ViewModel para la sesión del usuario con DataStore
class SesionViewModel(application: Application) : AndroidViewModel(application) {

    // DataStore creado con contexto de aplicacion
    private val sesionDataStore = SesionDataStore(application)

    // null significa que todavia se está leyendo del telefono
    private val _sesionActiva = MutableStateFlow<Boolean?>(null)
    val sesionActiva: StateFlow<Boolean?> = _sesionActiva

    private val _correo = MutableStateFlow<String?>(null)
    val correo: StateFlow<String?> = _correo

    init {
        // Al iniciar el ViewModel se carga la sesion desde DataStore
        cargarSesion()
    }

    private fun cargarSesion() {
        viewModelScope.launch {
            // Si nunca se guardó nada, se considera que no hay sesión
            _sesionActiva.value = sesionDataStore.obtenerSesion().first() ?: false
            _correo.value = sesionDataStore.obtenerCorreo().first()
        }
    }

    fun iniciarSesion(correo: String) {
        viewModelScope.launch {
            // Guardamos en DataStore y actualizamos el flujo
            sesionDataStore.guardarSesion(correo)
            _correo.value = correo
            _sesionActiva.value = true
        }
    }

    fun cerrarSesion() {
        viewModelScope.launch {
            sesionDataStore.cerrarSesion()
            _correo.value = null
            _sesionActiva.value = false
        }
    }
}