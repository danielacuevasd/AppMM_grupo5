package com.example.appmm_grupo5.viewmodel

import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.Assert.assertEquals

class UsuarioViewModelTest {

    @Test
    fun registro_vacio_no_es_valido_y_marca_errores() {
        val vm = UsuarioViewModel()
        assertFalse(vm.validarRegistro())
        assertNotNull(vm.registro.value.errores.nombre)
        assertNotNull(vm.registro.value.errores.aceptaTerminos)
    }

    @Test
    fun registro_completo_es_valido() {
        val vm = UsuarioViewModel()
        vm.onNombreChange("Camila Rojas")
        vm.onCorreoChange("camila@negocio.cl")
        vm.onTelefonoChange("912345678")
        vm.onClaveChange("clave1234")
        vm.onConfirmarClaveChange("clave1234")
        vm.onTerminosChange(true)
        assertTrue(vm.validarRegistro())
    }

    @Test
    fun error_de_correo_aparece_al_escribir_y_se_quita_al_corregir() {
        val vm = UsuarioViewModel()
        vm.onCorreoChange("camila@negocio")
        assertNotNull(vm.registro.value.errores.correo)
        vm.onCorreoChange("camila@negocio.cl")
        assertNull(vm.registro.value.errores.correo)
    }

    @Test
    fun cambiar_la_clave_vuelve_a_revisar_la_confirmacion() {
        val vm = UsuarioViewModel()
        vm.onClaveChange("clave1234")
        vm.onConfirmarClaveChange("clave1234")
        assertNull(vm.registro.value.errores.confirmarClave)
        vm.onClaveChange("otraClave99")
        assertNotNull(vm.registro.value.errores.confirmarClave)
    }

    @Test
    fun login_con_clave_vacia_no_es_valido() {
        val vm = UsuarioViewModel()
        vm.onLoginCorreoChange("camila@negocio.cl")
        assertFalse(vm.validarLogin())
        assertNotNull(vm.login.value.errores.clave)
    }

    // Comprueba que al cerrar sesion los campos queden vacios
    @Test
    fun reiniciar_formularios_deja_login_y_registro_vacios() {
        val vm = UsuarioViewModel()
        vm.onLoginCorreoChange("camila@negocio.cl")
        vm.onLoginClaveChange("clave1234")
        vm.onNombreChange("Camila Rojas")

        vm.reiniciarFormularios()

        assertEquals("", vm.login.value.correo)
        assertEquals("", vm.login.value.clave)
        assertEquals("", vm.registro.value.nombre)
    }
}