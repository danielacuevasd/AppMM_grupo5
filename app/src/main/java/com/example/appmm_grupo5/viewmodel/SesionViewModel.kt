package com.example.appmm_grupo5.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmm_grupo5.data.AppDatabase
import com.example.appmm_grupo5.data.SesionDataStore
import com.example.appmm_grupo5.model.EncuestaUiState
import com.example.appmm_grupo5.model.PerfilUiState
import com.example.appmm_grupo5.repository.PerfilRepository
import com.example.appmm_grupo5.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

// ViewModel para la sesión del usuario con DataStore
class SesionViewModel(application: Application) : AndroidViewModel(application) {

    // DataStore creado con contexto de aplicacion
    private val sesionDataStore = SesionDataStore(application)

    private val usuarioRepositorio = UsuarioRepository(AppDatabase.obtener(application).usuarioDao())

    private val perfilRepositorio = PerfilRepository(
        AppDatabase.obtener(application).perfilNegocioDao(),
        AppDatabase.obtener(application).encuestaDao()
    )

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
            val activa = sesionDataStore.obtenerSesion().first() ?: false
            val id = sesionDataStore.obtenerUsuarioId().first()
            // Usuario guardado con version anterior ya no sirve
            val idValido = activa && id != null && id > 0L
            val usuarioExiste = idValido && usuarioRepositorio.buscarPorId(id!!) != null
            if (activa && !usuarioExiste) sesionDataStore.cerrarSesion()
            _sesionActiva.value = usuarioExiste
            _usuarioId.value = if (usuarioExiste) id else null
            _correo.value = if (usuarioExiste) sesionDataStore.obtenerCorreo().first() else null
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

    fun guardarPerfil(perfil: PerfilUiState) {
        val id = _usuarioId.value ?: return
        if (id <= 0L) return
        viewModelScope.launch { perfilRepositorio.guardarPerfil(id, perfil) }
    }

    fun guardarEncuesta(encuesta: EncuestaUiState) {
        val id = _usuarioId.value ?: return
        if (id <= 0L) return
        viewModelScope.launch { perfilRepositorio.guardarEncuesta(id, encuesta) }
    }

    fun cargarDatosUsuario(
        usuarioId: Long,
        onResultado: (PerfilUiState?, EncuestaUiState?) -> Unit
    ) {
        viewModelScope.launch {
            val perfil = perfilRepositorio.cargarPerfil(usuarioId)
            val encuesta = perfilRepositorio.cargarEncuesta(usuarioId)
            onResultado(perfil, encuesta)
        }
    }
    // Guarda la foto de perfil de forma permanente
    fun guardarFotoPerfil(origen: Uri, onResultado: (Uri) -> Unit) {
        val id = _usuarioId.value
        if (id == null || id <= 0L) {
            onResultado(origen)
            return
        }
        viewModelScope.launch {
            val copia = withContext(Dispatchers.IO) { copiarFoto(origen, id) }
            onResultado(copia ?: origen)
        }
    }
    // Carga la foto de perfil desde el dispositivo
    fun cargarFotoPerfil(usuarioId: Long, onResultado: (Uri?) -> Unit) {
        viewModelScope.launch {
            val foto = withContext(Dispatchers.IO) {
                File(getApplication<Application>().filesDir, "fotos")
                    .listFiles { archivo -> archivo.name.startsWith("perfil_${usuarioId}_") }
                    ?.maxByOrNull { it.lastModified() }
            }
            onResultado(foto?.let { Uri.fromFile(it) })
        }
    }
    // Copia la foto al directorio de archivos de la aplicacion
    private fun copiarFoto(origen: Uri, usuarioId: Long): Uri? {
        return try {
            val contexto = getApplication<Application>()
            val carpeta = File(contexto.filesDir, "fotos").apply { mkdirs() }
            val destino = File(carpeta, "perfil_${usuarioId}_${System.currentTimeMillis()}.jpg")
            val entrada = contexto.contentResolver.openInputStream(origen) ?: return null
            entrada.use { lectura -> destino.outputStream().use { escritura -> lectura.copyTo(escritura) } }
            carpeta.listFiles { archivo ->
                archivo.name.startsWith("perfil_${usuarioId}_") && archivo != destino
            }?.forEach { it.delete() }
            Uri.fromFile(destino)
        } catch (e: Exception) {
            null
        }
    }

}