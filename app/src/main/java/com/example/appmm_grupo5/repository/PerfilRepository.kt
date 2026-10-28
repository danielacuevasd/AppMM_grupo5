package com.example.appmm_grupo5.repository

import com.example.appmm_grupo5.data.EncuestaDao
import com.example.appmm_grupo5.data.PerfilNegocioDao
import com.example.appmm_grupo5.model.Encuesta
import com.example.appmm_grupo5.model.EncuestaUiState
import com.example.appmm_grupo5.model.PerfilNegocio
import com.example.appmm_grupo5.model.PerfilUiState

// Traduce entre lo que se muestra y lo que se guarda en la base de datos.
class PerfilRepository(
    private val perfilDao: PerfilNegocioDao,
    private val encuestaDao: EncuestaDao
) {

    // El ya esta validado
    suspend fun guardarPerfil(usuarioId: Long, perfil: PerfilUiState) {
        perfilDao.guardar(
            PerfilNegocio(
                usuarioId = usuarioId,
                nombreNegocio = perfil.nombreNegocio.trim(),
                rutEmpresa = perfil.rutEmpresa.trim(),
                tipoNegocio = perfil.tipoNegocio,
                empleados = perfil.empleados.trim().toInt(),
                direccion = perfil.direccion.trim()
            )
        )
    }

    // Trae el perfil guardado listo para rellenar el formulario, o null si todavia no existe
    suspend fun cargarPerfil(usuarioId: Long): PerfilUiState? {
        val guardado = perfilDao.obtener(usuarioId) ?: return null
        return PerfilUiState(
            nombreNegocio = guardado.nombreNegocio,
            rutEmpresa = guardado.rutEmpresa,
            tipoNegocio = guardado.tipoNegocio,
            empleados = guardado.empleados.toString(),
            direccion = guardado.direccion
        )
    }

    // La encuesta ya viene validada tambien
    suspend fun guardarEncuesta(usuarioId: Long, encuesta: EncuestaUiState) {
        encuestaDao.guardar(
            Encuesta(
                usuarioId = usuarioId,
                intereses = Encuesta.unirIntereses(encuesta.intereses),
                objetivo = encuesta.objetivo,
                recibeNovedades = encuesta.recibeNovedades
            )
        )
    }

    // Trae la encuesta guardada lista para rellenar la pantalla, o null si todavia no existe
    suspend fun cargarEncuesta(usuarioId: Long): EncuestaUiState? {
        val guardada = encuestaDao.obtener(usuarioId) ?: return null
        return EncuestaUiState(
            intereses = guardada.listaIntereses(),
            objetivo = guardada.objetivo,
            recibeNovedades = guardada.recibeNovedades
        )
    }
}