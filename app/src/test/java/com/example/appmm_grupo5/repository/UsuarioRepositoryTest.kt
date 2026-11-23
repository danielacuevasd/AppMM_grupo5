package com.example.appmm_grupo5.repository

import com.example.appmm_grupo5.network.RegistroRequest
import com.example.appmm_grupo5.network.UsuarioApi
import com.example.appmm_grupo5.network.UsuarioResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.io.IOException

class UsuarioRepositoryTest {

    private lateinit var api: UsuarioApi
    private lateinit var repositorio: UsuarioRepository

    private val respuesta = UsuarioResponse(1L, "Camila", "camila@negocio.cl", "+56912345678")

    private fun <T> error(codigo: Int): Response<T> =
        Response.error(codigo, "".toResponseBody("application/json".toMediaType()))

    @Before
    fun preparar() {
        api = mockk()
        repositorio = UsuarioRepository(api)
    }

    @Test
    fun registrar_correcto_devuelve_exito_y_limpia_el_correo() = runTest {
        val enviado = slot<RegistroRequest>()
        coEvery { api.registrar(capture(enviado)) } returns Response.success(201, respuesta)

        val resultado = repositorio.registrar(" Camila ", "  CAMILA@Negocio.cl ", " +56912345678 ", "clave1234")

        assertTrue(resultado is ResultadoUsuario.Exito)
        assertEquals(1L, (resultado as ResultadoUsuario.Exito).usuario.id)
        assertEquals("camila@negocio.cl", enviado.captured.correo)
        assertEquals("Camila", enviado.captured.nombre)
    }

    @Test
    fun registrar_con_correo_repetido_devuelve_CorreoRepetido() = runTest {
        coEvery { api.registrar(any()) } returns error(409)
        assertEquals(
            ResultadoUsuario.CorreoRepetido,
            repositorio.registrar("Camila", "camila@negocio.cl", "+56912345678", "clave1234")
        )
    }

    @Test
    fun login_correcto_devuelve_exito() = runTest {
        coEvery { api.login(any()) } returns Response.success(respuesta)
        val resultado = repositorio.iniciarSesion("camila@negocio.cl", "clave1234")
        assertTrue(resultado is ResultadoUsuario.Exito)
    }

    @Test
    fun login_con_clave_incorrecta_devuelve_CredencialesIncorrectas() = runTest {
        coEvery { api.login(any()) } returns error(401)
        assertEquals(
            ResultadoUsuario.CredencialesIncorrectas,
            repositorio.iniciarSesion("camila@negocio.cl", "mala")
        )
    }

    @Test
    fun buscarPorId_inexistente_devuelve_NoEncontrado() = runTest {
        coEvery { api.obtener(99L) } returns error(404)
        assertEquals(ResultadoUsuario.NoEncontrado, repositorio.buscarPorId(99L))
        coVerify(exactly = 1) { api.obtener(99L) }
    }

    @Test
    fun sin_conexion_devuelve_ErrorRed() = runTest {
        coEvery { api.login(any()) } throws IOException("sin red")
        assertEquals(ResultadoUsuario.ErrorRed, repositorio.iniciarSesion("a@b.cl", "clave1234"))
    }

    @Test
    fun codigo_500_devuelve_ErrorServidor() = runTest {
        coEvery { api.obtener(any()) } returns error(500)
        assertEquals(ResultadoUsuario.ErrorServidor, repositorio.buscarPorId(1L))
    }

    @Test
    fun actualizar_correcto_devuelve_exito() = runTest {
        coEvery { api.actualizar(1L, any()) } returns Response.success(respuesta)
        val resultado = repositorio.actualizar(1L, " Camila ", "+56912345678", null)
        assertTrue(resultado is ResultadoUsuario.Exito)
    }

    @Test
    fun actualizar_usuario_inexistente_devuelve_NoEncontrado() = runTest {
        coEvery { api.actualizar(any(), any()) } returns error(404)
        assertEquals(ResultadoUsuario.NoEncontrado, repositorio.actualizar(9L, "Camila", "+56912345678", null))
    }

    @Test
    fun eliminar_correcto_devuelve_Eliminado() = runTest {
        coEvery { api.eliminar(1L) } returns Response.success(204, Unit)
        assertEquals(ResultadoUsuario.Eliminado, repositorio.eliminar(1L))
    }

    @Test
    fun eliminar_sin_conexion_devuelve_ErrorRed() = runTest {
        coEvery { api.eliminar(any()) } throws IOException("sin red")
        assertEquals(ResultadoUsuario.ErrorRed, repositorio.eliminar(1L))
    }
}