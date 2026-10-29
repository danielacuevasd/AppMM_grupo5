package com.example.appmm_grupo5.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmm_grupo5.data.AppDatabase
import com.example.appmm_grupo5.data.SesionDataStore
import com.example.appmm_grupo5.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// ViewModel para la sesión del usuario con DataStore
class SesionViewModel(application: Application) : AndroidViewModel(application) {

    // DataStore creado con contexto de aplicacion
    private val sesionDataStore = SesionDataStore(application)

    private val usuarioRepositorio = UsuarioRepository(AppDatabase.obtener(application).usuarioDao())

    // null significa que todavia se está leyendo del telefono
    private val _sesionActiva = MutableStateFlow<Boolean?>(null)
    val sesionActiva: StateFlow<Boolean?> = _sesionActiva

    private val _usuarioId = MutableStateFlow<Long?>(null)
    val usuarioId: StateFlow<Long?> = _usuarioId

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
            _usuarioId.value = sesionDataStore.obtenerUsuarioId().first()
            _correo.value = sesionDataStore.obtenerCorreo().first()
        }
    }

    fun iniciarSesion(usuarioId: Long, correo: String) {
        viewModelScope.launch {
            // Guardamos en DataStore y actualizamos el flujo
            sesionDataStore.guardarSesion(usuarioId, correo)
            _usuarioId.value = usuarioId
            _correo.value = correo
            _sesionActiva.value = true
        }
    }

    fun cerrarSesion() {
        viewModelScope.launch {
            sesionDataStore.cerrarSesion()
            _usuarioId.value = null
            _correo.value = null
            _sesionActiva.value = false
        }
    }

    fun registrar(
        nombre: String,
        correo: String,
        telefono: String,
        clave: String,
        onResultado: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val usuario = usuarioRepositorio.registrar(nombre, correo, telefono, clave)
            if (usuario != null) iniciarSesion(usuario.id, usuario.correo)
            onResultado(usuario != null)
        }
    }

    fun ingresar(correo: String, clave: String, onResultado: (Boolean) -> Unit) {
        viewModelScope.launch {
            val usuario = usuarioRepositorio.iniciarSesion(correo, clave)
            if (usuario != null) iniciarSesion(usuario.id, usuario.correo)
            onResultado(usuario != null)
        }
    }
}