package com.example.appmm_grupo5.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.longPreferencesKey

val Context.sesionDataStore by preferencesDataStore(name = "sesion_usuario")

// Guarda en el telefono la sesion iniciado
class SesionDataStore(private val context: Context) {

    // Datos de la sesion
    private val SESION_ACTIVA = booleanPreferencesKey("sesion_activa")
    private val CORREO_USUARIO = stringPreferencesKey("correo_usuario")
    private val USUARIO_ID = longPreferencesKey("usuario_id")

    // Con esto se guarda la sesion en dataStore
    suspend fun guardarSesion(usuarioId: Long, correo: String) {
        context.sesionDataStore.edit { preferencias ->
            preferencias[SESION_ACTIVA] = true
            preferencias[USUARIO_ID] = usuarioId
            preferencias[CORREO_USUARIO] = correo
        }
    }

    // Cuando se cierra la sesion se borra del dataStore
    suspend fun cerrarSesion() {
        context.sesionDataStore.edit { preferencias ->
            preferencias[SESION_ACTIVA] = false
            preferencias.remove(USUARIO_ID)
            preferencias.remove(CORREO_USUARIO)
        }
    }

    // Obtiene los datos del FLow de sesion
    fun obtenerSesion(): Flow<Boolean?> {
        return context.sesionDataStore.data.map { preferencias ->
            preferencias[SESION_ACTIVA]
        }
    }

    fun obtenerUsuarioId(): Flow<Long?> {
        return context.sesionDataStore.data.map { preferencias ->
            preferencias[USUARIO_ID]
        }
    }

    fun obtenerCorreo(): Flow<String?> {
        return context.sesionDataStore.data.map { preferencias ->
            preferencias[CORREO_USUARIO]
        }
    }
}