package com.example.appmm_grupo5.viewmodel

import com.example.appmm_grupo5.model.OpcionesPerfil
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PerfilViewModelTest {

    @Test
    fun perfil_vacio_no_es_valido_y_marca_errores() {
        val vm = PerfilViewModel()
        assertFalse(vm.validarPerfil())
        assertNotNull(vm.perfil.value.errores.nombreNegocio)
        assertNotNull(vm.perfil.value.errores.tipoNegocio)
    }

    @Test
    fun perfil_completo_es_valido() {
        val vm = PerfilViewModel()
        vm.onNombreNegocioChange("Pastelería Dulce Hogar")
        vm.onRutEmpresaChange("76.345.678-1")
        vm.onTipoNegocioChange("Pastelería")
        vm.onEmpleadosChange("8")
        vm.onDireccionChange("Avenida Kennedy 123")
        assertTrue(vm.validarPerfil())
    }

    @Test
    fun error_de_rut_aparece_al_escribir_y_se_quita_al_corregir() {
        val vm = PerfilViewModel()
        vm.onRutEmpresaChange("76.345.678-5")
        assertNotNull(vm.perfil.value.errores.rutEmpresa)
        vm.onRutEmpresaChange("76.345.678-1")
        assertNull(vm.perfil.value.errores.rutEmpresa)
    }

    @Test
    fun el_tamano_cambia_al_escribir_la_cantidad_de_personas() {
        val vm = PerfilViewModel()
        vm.onEmpleadosChange("3")
        assertEquals(OpcionesPerfil.tamanosNegocio[0], vm.perfil.value.tamanoNegocio)
        vm.onEmpleadosChange("30")
        assertEquals(OpcionesPerfil.tamanosNegocio[2], vm.perfil.value.tamanoNegocio)
    }

    @Test
    fun encuesta_sin_intereses_no_es_valida() {
        val vm = PerfilViewModel()
        vm.onObjetivoChange(OpcionesPerfil.objetivos[0])
        assertFalse(vm.validarEncuesta())
        assertNotNull(vm.encuesta.value.errores.intereses)
    }

    @Test
    fun marcar_y_desmarcar_intereses() {
        val vm = PerfilViewModel()
        vm.onInteresChange("Pastelería", true)
        vm.onInteresChange("Horneo", true)
        assertEquals(setOf("Pastelería", "Horneo"), vm.encuesta.value.intereses)
        vm.onInteresChange("Horneo", false)
        assertEquals(setOf("Pastelería"), vm.encuesta.value.intereses)
    }

    @Test
    fun encuesta_completa_es_valida() {
        val vm = PerfilViewModel()
        vm.onInteresChange("Galletas", true)
        vm.onObjetivoChange(OpcionesPerfil.objetivos[1])
        assertTrue(vm.validarEncuesta())
        assertTrue(vm.encuesta.value.recibeNovedades)
    }
}