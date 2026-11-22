package com.example.appmm_grupo5.repository

import com.example.appmm_grupo5.network.CatalogoApi
import com.example.appmm_grupo5.network.ContenidoResponse
import com.example.appmm_grupo5.network.FavoritoRequest
import com.example.appmm_grupo5.network.FavoritoResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.io.IOException

class ContenidoRepositoryTest {

    private lateinit var api: CatalogoApi
    private lateinit var repositorio: ContenidoRepository

    private val sables = ContenidoResponse(1, "Receta", "Sables de cacao", "Galletas", "Galleta de cacao", 45, "Basico")
    private val hojaldre = ContenidoResponse(2, "Video", "Plegado de hojaldre", "Masas de hoja", "Tecnica de plegado", 12, "Avanzado")

    private fun <T> error(codigo: Int): Response<T> =
        Response.error(codigo, "".toResponseBody("application/json".toMediaType()))

    @Before
    fun preparar() {
        api = mockk()
        repositorio = ContenidoRepository(api)
    }

    @Test
    fun filtrar_correcto_convierte_las_respuestas_a_modelo() = runTest {
        coEvery { api.listar(null, null) } returns Response.success(listOf(sables, hojaldre))
        val resultado = repositorio.filtrar()
        assertTrue(resultado is ResultadoCatalogo.Exito)
        val lista = (resultado as ResultadoCatalogo.Exito).datos
        assertEquals(2, lista.size)
        assertEquals(1L, lista[0].id)
        assertEquals("Sables de cacao", lista[0].titulo)
        assertEquals(12, lista[1].duracionMinutos)
    }

    @Test
    fun filtrar_envia_el_tipo_y_la_categoria_al_servidor() = runTest {
        coEvery { api.listar("Video", "Masas de hoja") } returns Response.success(listOf(hojaldre))
        val resultado = repositorio.filtrar("Video", "Masas de hoja")
        assertEquals(1, (resultado as ResultadoCatalogo.Exito).datos.size)
        coVerify(exactly = 1) { api.listar("Video", "Masas de hoja") }
    }

    @Test
    fun filtrar_con_lista_vacia_devuelve_exito_sin_datos() = runTest {
        coEvery { api.listar(any(), any()) } returns Response.success(emptyList())
        val resultado = repositorio.filtrar("Curso", null)
        assertEquals(emptyList<Any>(), (resultado as ResultadoCatalogo.Exito).datos)
    }

    @Test
    fun filtrar_sin_internet_devuelve_ErrorRed() = runTest {
        coEvery { api.listar(any(), any()) } throws IOException("sin red")
        assertEquals(ResultadoCatalogo.ErrorRed, repositorio.filtrar())
    }

    @Test
    fun filtrar_con_error_500_devuelve_ErrorServidor() = runTest {
        coEvery { api.listar(any(), any()) } returns error(500)
        assertEquals(ResultadoCatalogo.ErrorServidor, repositorio.filtrar())
    }

    @Test
    fun buscar_por_id_correcto_devuelve_el_contenido() = runTest {
        coEvery { api.obtener(1) } returns Response.success(sables)
        val resultado = repositorio.buscarPorId(1)
        assertEquals("Galletas", (resultado as ResultadoCatalogo.Exito).datos.categoria)
    }

    @Test
    fun buscar_por_id_inexistente_devuelve_NoEncontrado() = runTest {
        coEvery { api.obtener(99) } returns error(404)
        assertEquals(ResultadoCatalogo.NoEncontrado, repositorio.buscarPorId(99))
    }

    @Test
    fun buscar_por_id_con_error_500_devuelve_ErrorServidor() = runTest {
        coEvery { api.obtener(any()) } returns error(500)
        assertEquals(ResultadoCatalogo.ErrorServidor, repositorio.buscarPorId(1))
    }

    @Test
    fun buscar_por_id_sin_internet_devuelve_ErrorRed() = runTest {
        coEvery { api.obtener(any()) } throws IOException("sin red")
        assertEquals(ResultadoCatalogo.ErrorRed, repositorio.buscarPorId(1))
    }

    @Test
    fun favoritos_correcto_devuelve_los_contenidos_guardados() = runTest {
        coEvery { api.favoritosDeUsuario(7) } returns Response.success(listOf(hojaldre))
        val resultado = repositorio.favoritos(7)
        val lista = (resultado as ResultadoCatalogo.Exito).datos
        assertEquals(listOf(2L), lista.map { it.id })
    }

    @Test
    fun favoritos_sin_internet_devuelve_ErrorRed() = runTest {
        coEvery { api.favoritosDeUsuario(any()) } throws IOException("sin red")
        assertEquals(ResultadoCatalogo.ErrorRed, repositorio.favoritos(7))
    }

    @Test
    fun marcar_favorito_envia_el_usuario_y_el_contenido() = runTest {
        coEvery { api.agregarFavorito(FavoritoRequest(7, 1)) } returns Response.success(FavoritoResponse(7, 1))
        val resultado = repositorio.cambiarFavorito(7, 1, true)
        assertTrue(resultado is ResultadoCatalogo.Exito)
        coVerify(exactly = 1) { api.agregarFavorito(FavoritoRequest(7, 1)) }
        coVerify(exactly = 0) { api.quitarFavorito(any(), any()) }
    }

    @Test
    fun marcar_favorito_de_contenido_inexistente_devuelve_NoEncontrado() = runTest {
        coEvery { api.agregarFavorito(any()) } returns error(404)
        assertEquals(ResultadoCatalogo.NoEncontrado, repositorio.cambiarFavorito(7, 99, true))
    }

    @Test
    fun marcar_favorito_con_error_400_devuelve_ErrorServidor() = runTest {
        coEvery { api.agregarFavorito(any()) } returns error(400)
        assertEquals(ResultadoCatalogo.ErrorServidor, repositorio.cambiarFavorito(0, 1, true))
    }

    @Test
    fun marcar_favorito_sin_internet_devuelve_ErrorRed() = runTest {
        coEvery { api.agregarFavorito(any()) } throws IOException("sin red")
        assertEquals(ResultadoCatalogo.ErrorRed, repositorio.cambiarFavorito(7, 1, true))
    }

    @Test
    fun quitar_favorito_correcto_devuelve_exito() = runTest {
        coEvery { api.quitarFavorito(7, 1) } returns Response.success(Unit)
        val resultado = repositorio.cambiarFavorito(7, 1, false)
        assertTrue(resultado is ResultadoCatalogo.Exito)
        coVerify(exactly = 1) { api.quitarFavorito(7, 1) }
        coVerify(exactly = 0) { api.agregarFavorito(any()) }
    }

    @Test
    fun quitar_favorito_que_ya_no_existe_se_considera_exito() = runTest {
        coEvery { api.quitarFavorito(any(), any()) } returns error(404)
        assertTrue(repositorio.cambiarFavorito(7, 1, false) is ResultadoCatalogo.Exito)
    }

    @Test
    fun quitar_favorito_con_error_500_devuelve_ErrorServidor() = runTest {
        coEvery { api.quitarFavorito(any(), any()) } returns error(500)
        assertEquals(ResultadoCatalogo.ErrorServidor, repositorio.cambiarFavorito(7, 1, false))
    }

    @Test
    fun quitar_favorito_sin_internet_devuelve_ErrorRed() = runTest {
        coEvery { api.quitarFavorito(any(), any()) } throws IOException("sin red")
        assertEquals(ResultadoCatalogo.ErrorRed, repositorio.cambiarFavorito(7, 1, false))
    }

    @Test
    fun mensajes_de_error_segun_el_resultado() {
        assertNull(ResultadoCatalogo.Exito(Unit).mensajeError())
        assertEquals("No se encontró el contenido", ResultadoCatalogo.NoEncontrado.mensajeError())
        assertNotNull(ResultadoCatalogo.ErrorRed.mensajeError())
        assertNotNull(ResultadoCatalogo.ErrorServidor.mensajeError())
    }
}
