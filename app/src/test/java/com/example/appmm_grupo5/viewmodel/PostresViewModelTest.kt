package com.example.appmm_grupo5.viewmodel

import com.example.appmm_grupo5.model.Postre
import com.example.appmm_grupo5.repository.MealRepository
import com.example.appmm_grupo5.repository.ResultadoApi
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PostresViewModelTest {

    private lateinit var repositorio: MealRepository

    private val tarta = Postre("1", "Apple Tart", "img1")
    private val flan = Postre("2", "Flan", "img2")

    @Before
    fun preparar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        repositorio = mockk()
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    @Test
    fun al_iniciar_carga_los_postres() {
        coEvery { repositorio.obtenerPostres() } returns ResultadoApi.Exito(listOf(tarta, flan))
        val vm = PostresViewModel(repositorio)
        assertEquals(2, vm.estado.value.postres.size)
        assertFalse(vm.estado.value.cargando)
        assertNull(vm.estado.value.error)
    }

    @Test
    fun sin_internet_muestra_mensaje_de_error() {
        coEvery { repositorio.obtenerPostres() } returns ResultadoApi.ErrorRed
        val vm = PostresViewModel(repositorio)
        assertNotNull(vm.estado.value.error)
        assertEquals(0, vm.estado.value.postres.size)
    }

    @Test
    fun el_buscador_filtra_sin_distinguir_mayusculas() {
        coEvery { repositorio.obtenerPostres() } returns ResultadoApi.Exito(listOf(tarta, flan))
        val vm = PostresViewModel(repositorio)
        vm.onBusquedaChange("flAN")
        assertEquals(listOf(flan), vm.estado.value.visibles)
    }

    @Test
    fun abrir_detalle_carga_las_instrucciones() {
        coEvery { repositorio.obtenerPostres() } returns ResultadoApi.Exito(listOf(tarta))
        coEvery { repositorio.obtenerDetalle("1") } returns
                ResultadoApi.Exito(tarta.copy(instrucciones = "Hornear 30 minutos"))
        val vm = PostresViewModel(repositorio)
        vm.abrirDetalle(tarta)
        assertEquals("Hornear 30 minutos", vm.estado.value.detalle?.instrucciones)
        assertFalse(vm.estado.value.cargandoDetalle)
    }

    @Test
    fun cerrar_detalle_lo_limpia() {
        coEvery { repositorio.obtenerPostres() } returns ResultadoApi.Exito(listOf(tarta))
        coEvery { repositorio.obtenerDetalle(any()) } returns ResultadoApi.SinDatos
        val vm = PostresViewModel(repositorio)
        vm.abrirDetalle(tarta)
        vm.cerrarDetalle()
        assertNull(vm.estado.value.detalle)
    }
}