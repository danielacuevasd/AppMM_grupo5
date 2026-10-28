package com.example.appmm_grupo5.repository

import com.example.appmm_grupo5.data.Seguridad
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class UsuarioRepositoryTest {

    private val dao = UsuarioDaoTest()
    private val repositorio = UsuarioRepository(dao)

    @Test
    fun registrar_guarda_el_usuario_con_la_clave_en_hash() = runBlocking {
        val usuario = repositorio.registrar("Camila Rojas", "camila@negocio.cl", "912345678", "clave1234")

        assertNotNull(usuario)
        assertNotEquals("clave1234", usuario!!.claveHash)
        assertEquals(Seguridad.sha256("clave1234"), usuario.claveHash)
        assertEquals(1, dao.usuarios.size)
    }

    @Test
    fun registrar_con_un_correo_repetido_devuelve_null() = runBlocking {
        repositorio.registrar("Camila Rojas", "camila@negocio.cl", "912345678", "clave1234")
        val repetido = repositorio.registrar("Otra Persona", "CAMILA@negocio.cl", "987654321", "otraClave99")

        assertNull(repetido)
        assertEquals(1, dao.usuarios.size)
    }

    @Test
    fun iniciar_sesion_con_datos_correctos_devuelve_el_usuario() = runBlocking {
        repositorio.registrar("Camila Rojas", "camila@negocio.cl", "912345678", "clave1234")

        val usuario = repositorio.iniciarSesion("  Camila@Negocio.cl ", "clave1234")

        assertNotNull(usuario)
        assertEquals("Camila Rojas", usuario!!.nombre)
    }

    @Test
    fun iniciar_sesion_con_clave_incorrecta_devuelve_null() = runBlocking {
        repositorio.registrar("Camila Rojas", "camila@negocio.cl", "912345678", "clave1234")

        assertNull(repositorio.iniciarSesion("camila@negocio.cl", "claveEquivocada1"))
    }

    @Test
    fun iniciar_sesion_con_un_correo_que_no_existe_devuelve_null() = runBlocking {
        assertNull(repositorio.iniciarSesion("nadie@negocio.cl", "clave1234"))
    }
}