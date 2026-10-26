package com.example.appmm_grupo5.validation

import com.example.appmm_grupo5.model.EncuestaUiState
import com.example.appmm_grupo5.model.OpcionesPerfil
import com.example.appmm_grupo5.model.PerfilUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidadoresPerfilTest {

    @Test
    fun nombre_de_negocio_vacio_tiene_error() {
        assertNotNull(Validadores.nombreNegocio("   "))
    }

    @Test
    fun nombre_de_negocio_valido_no_tiene_error() {
        assertNull(Validadores.nombreNegocio("Pastelería Dulce Hogar"))
    }

    @Test
    fun direccion_sin_numero_tiene_error() {
        assertNotNull(Validadores.direccion("Avenida Kennedy"))
    }

    @Test
    fun direccion_con_numero_es_valida() {
        assertNull(Validadores.direccion("Avenida Kennedy 123"))
    }

    @Test
    fun empleados_que_no_es_numero_tiene_error() {
        assertNotNull(Validadores.empleados("muchos"))
    }

    @Test
    fun empleados_fuera_del_rango_general_tiene_error() {
        assertNotNull(Validadores.empleados("0"))
        assertNotNull(Validadores.empleados("501"))
    }

    @Test
    fun empleados_dentro_del_rango_es_valido() {
        assertNull(Validadores.empleados("1"))
        assertNull(Validadores.empleados("30"))
    }

    @Test
    fun el_tamano_se_calcula_segun_la_cantidad_de_personas() {
        assertEquals(OpcionesPerfil.tamanosNegocio[0], PerfilUiState(empleados = "3").tamanoNegocio)
        assertEquals(OpcionesPerfil.tamanosNegocio[1], PerfilUiState(empleados = "10").tamanoNegocio)
        assertEquals(OpcionesPerfil.tamanosNegocio[2], PerfilUiState(empleados = "30").tamanoNegocio)
        assertEquals("", PerfilUiState(empleados = "abc").tamanoNegocio)
    }

    @Test
    fun encuesta_sin_intereses_tiene_error() {
        assertNotNull(Validadores.intereses(emptySet()))
        assertNull(Validadores.intereses(setOf("Pastelería")))
    }

    @Test
    fun perfil_completo_y_correcto_no_tiene_errores() {
        val estado = PerfilUiState(
            nombreNegocio = "Pastelería Dulce Hogar",
            rutEmpresa = "76.345.678-1",
            tipoNegocio = "Pastelería",
            empleados = "5",
            direccion = "Avenida Kennedy 123"
        )
        assertFalse(Validadores.validarPerfil(estado).hayErrores())
    }

    @Test
    fun perfil_vacio_marca_todos_los_campos() {
        val errores = Validadores.validarPerfil(PerfilUiState())
        assertEquals(5, errores.mensajes().size)
    }

    @Test
    fun encuesta_completa_no_tiene_errores() {
        val estado = EncuestaUiState(
            intereses = setOf("Pastelería", "Horneo"),
            objetivo = OpcionesPerfil.objetivos[0]
        )
        assertFalse(Validadores.validarEncuesta(estado).hayErrores())
    }

    @Test
    fun encuesta_vacia_marca_intereses_y_objetivo() {
        assertTrue(Validadores.validarEncuesta(EncuestaUiState()).hayErrores())
    }
}