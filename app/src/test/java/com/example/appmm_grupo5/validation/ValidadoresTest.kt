package com.example.appmm_grupo5.validation

import com.example.appmm_grupo5.model.FormularioUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidadoresTest {

    @Test
    fun correo_valido_no_tiene_error() {
        assertNull(Validadores.correo("camila@negocio.cl"))
    }

    @Test
    fun correo_sin_arroba_tiene_error() {
        assertNotNull(Validadores.correo("camilanegocio.cl"))
    }

    @Test
    fun correo_sin_dominio_tiene_error() {
        assertNotNull(Validadores.correo("camila@negocio"))
    }

    @Test
    fun rut_valido_con_puntos_y_guion() {
        assertNull(Validadores.rut("12.345.678-5"))
    }

    @Test
    fun rut_valido_con_digito_k() {
        assertNull(Validadores.rut("6.000.000-K"))
    }

    @Test
    fun rut_con_digito_incorrecto_tiene_error() {
        assertNotNull(Validadores.rut("12.345.678-9"))
    }

    @Test
    fun telefono_valido_con_y_sin_prefijo() {
        assertNull(Validadores.telefono("912345678"))
        assertNull(Validadores.telefono("+56 9 1234 5678"))
    }

    @Test
    fun telefono_corto_tiene_error() {
        assertNotNull(Validadores.telefono("12345"))
    }

    @Test
    fun clave_sin_numeros_tiene_error() {
        assertNotNull(Validadores.clave("soloLetras"))
    }

    @Test
    fun claves_distintas_tienen_error() {
        assertEquals("Las claves no coinciden", Validadores.confirmarClave("clave1234", "clave1235"))
    }

    @Test
    fun rango_fuera_de_limites_tiene_error() {
        assertNotNull(Validadores.rango("0", 1, 200, "La cantidad de personas"))
        assertNull(Validadores.rango("5", 1, 200, "La cantidad de personas"))
    }

    @Test
    fun registro_vacio_tiene_errores() {
        val errores = Validadores.validarRegistro(FormularioUiState())
        assertTrue(errores.hayErrores())
        assertNotNull(errores.nombre)
        assertNotNull(errores.aceptaTerminos)
    }

    @Test
    fun registro_completo_no_tiene_errores() {
        val estado = FormularioUiState(
            nombre = "Camila Rojas",
            correo = "camila@negocio.cl",
            telefono = "912345678",
            clave = "clave1234",
            confirmarClave = "clave1234",
            aceptaTerminos = true
        )
        assertFalse(Validadores.validarRegistro(estado).hayErrores())
    }
}