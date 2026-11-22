package com.example.appmm_grupo5.repository

import com.example.appmm_grupo5.network.MealApiService
import com.example.appmm_grupo5.network.MealDto
import com.example.appmm_grupo5.network.MealListaResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.io.IOException

class MealRepositoryTest {

    private lateinit var api: MealApiService
    private lateinit var repositorio: MealRepository

    private val dto = MealDto("52776", "Chocolate Gateau", "http://img/1.jpg", "Dessert", "French", "Mezclar y hornear")

    private fun <T> error(codigo: Int): Response<T> =
        Response.error(codigo, "".toResponseBody("application/json".toMediaType()))

    @Before
    fun preparar() {
        api = mockk()
        repositorio = MealRepository(api)
    }

    @Test
    fun postres_correctos_se_convierten_a_modelo() = runTest {
        coEvery { api.postres("Dessert") } returns Response.success(MealListaResponse(listOf(dto)))
        val resultado = repositorio.obtenerPostres()
        assertTrue(resultado is ResultadoApi.Exito)
        val postre = (resultado as ResultadoApi.Exito).datos.first()
        assertEquals("52776", postre.id)
        assertEquals("Chocolate Gateau", postre.nombre)
    }

    @Test
    fun postres_con_meals_null_devuelve_lista_vacia() = runTest {
        coEvery { api.postres(any()) } returns Response.success(MealListaResponse(null))
        val resultado = repositorio.obtenerPostres()
        assertEquals(emptyList<Any>(), (resultado as ResultadoApi.Exito).datos)
    }

    @Test
    fun postres_sin_nombre_usa_texto_por_defecto() = runTest {
        val sinNombre = dto.copy(strMeal = null, strArea = null)
        coEvery { api.postres(any()) } returns Response.success(MealListaResponse(listOf(sinNombre)))
        val postre = (repositorio.obtenerPostres() as ResultadoApi.Exito).datos.first()
        assertEquals("Sin nombre", postre.nombre)
    }

    @Test
    fun postres_sin_internet_devuelve_ErrorRed() = runTest {
        coEvery { api.postres(any()) } throws IOException("sin red")
        assertEquals(ResultadoApi.ErrorRed, repositorio.obtenerPostres())
    }

    @Test
    fun postres_con_error_500_devuelve_ErrorServidor() = runTest {
        coEvery { api.postres(any()) } returns error(500)
        assertEquals(ResultadoApi.ErrorServidor, repositorio.obtenerPostres())
    }

    @Test
    fun detalle_correcto_devuelve_el_postre() = runTest {
        coEvery { api.detalle("52776") } returns Response.success(MealListaResponse(listOf(dto)))
        val resultado = repositorio.obtenerDetalle("52776")
        assertEquals("Mezclar y hornear", (resultado as ResultadoApi.Exito).datos.instrucciones)
    }

    @Test
    fun detalle_con_meals_null_devuelve_SinDatos() = runTest {
        coEvery { api.detalle(any()) } returns Response.success(MealListaResponse(null))
        assertEquals(ResultadoApi.SinDatos, repositorio.obtenerDetalle("0"))
    }
}