package com.example.appmm_grupo5.repository

import com.example.appmm_grupo5.model.EncuestaUiState
import com.example.appmm_grupo5.model.PerfilUiState
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PerfilRepositoryTest {

    private val repositorio = PerfilRepository(PerfilNegocioDaoTest(), EncuestaDaoTest())

    @Test
    fun cargar_perfil_sin_datos_devuelve_null() = runBlocking {
        assertNull(repositorio.cargarPerfil(1))
    }

    @Test
    fun el_perfil_guardado_se_puede_volver_a_cargar() = runBlocking {
        val perfil = PerfilUiState(
            nombreNegocio = "Pastelería Dulce Hogar",
            rutEmpresa = "76.345.678-1",
            tipoNegocio = "Pastelería",
            empleados = "8",
            direccion = "Avenida Kennedy 123"
        )

        repositorio.guardarPerfil(1, perfil)
        val cargado = repositorio.cargarPerfil(1)

        assertEquals(perfil.nombreNegocio, cargado?.nombreNegocio)
        assertEquals(perfil.rutEmpresa, cargado?.rutEmpresa)
        assertEquals(perfil.tipoNegocio, cargado?.tipoNegocio)
        assertEquals("8", cargado?.empleados)
        assertEquals(perfil.direccion, cargado?.direccion)
    }

    @Test
    fun cada_usuario_tiene_su_propio_perfil() = runBlocking {
        repositorio.guardarPerfil(1, PerfilUiState(nombreNegocio = "Negocio Uno", empleados = "3"))

        assertNull(repositorio.cargarPerfil(2))
    }

    @Test
    fun cargar_encuesta_sin_datos_devuelve_null() = runBlocking {
        assertNull(repositorio.cargarEncuesta(1))
    }

    @Test
    fun la_encuesta_guardada_se_puede_volver_a_cargar() = runBlocking {
        val encuesta = EncuestaUiState(
            intereses = setOf("Galletas", "Horneo"),
            objetivo = "Ampliar mi carta",
            recibeNovedades = false
        )

        repositorio.guardarEncuesta(1, encuesta)
        val cargada = repositorio.cargarEncuesta(1)

        assertEquals(setOf("Galletas", "Horneo"), cargada?.intereses)
        assertEquals("Ampliar mi carta", cargada?.objetivo)
        assertEquals(false, cargada?.recibeNovedades)
    }
}