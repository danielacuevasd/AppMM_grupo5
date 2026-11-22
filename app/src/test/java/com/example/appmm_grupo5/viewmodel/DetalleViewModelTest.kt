package com.example.appmm_grupo5.viewmodel

import com.example.appmm_grupo5.model.Contenido
import com.example.appmm_grupo5.repository.ContenidoRepository
import com.example.appmm_grupo5.repository.ResultadoCatalogo
import com.example.appmm_grupo5.repository.mensajeError
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetalleViewModelTest {

    private lateinit var repositorio: ContenidoRepository
    private lateinit var vm: DetalleViewModel

    private val sables = Contenido(1, "Receta", "Sables de cacao", "Galletas", "Galleta de cacao", 45, "Basico")
    private val hojaldre = Contenido(2, "Video", "Plegado de hojaldre", "Masas de hoja", "Tecnica de plegado", 12, "Avanzado")

    @Before
    fun preparar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        repositorio = mockk()
        vm = DetalleViewModel(repositorio)
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    @Test
    fun cargar_muestra_el_contenido() {
        coEvery { repositorio.buscarPorId(1) } returns ResultadoCatalogo.Exito(sables)
        coEvery { repositorio.favoritos(7) } returns ResultadoCatalogo.Exito(emptyList())
        vm.cargar(1, 7)
        assertEquals(sables, vm.contenido.value)
        assertFalse(vm.cargando.value)
        assertNull(vm.error.value)
    }

    @Test
    fun cargar_marca_favorito_si_el_contenido_esta_guardado() {
        coEvery { repositorio.buscarPorId(1) } returns ResultadoCatalogo.Exito(sables)
        coEvery { repositorio.favoritos(7) } returns ResultadoCatalogo.Exito(listOf(hojaldre, sables))
        vm.cargar(1, 7)
        assertTrue(vm.esFavorito.value)
    }

    @Test
    fun cargar_no_marca_favorito_si_no_esta_guardado() {
        coEvery { repositorio.buscarPorId(1) } returns ResultadoCatalogo.Exito(sables)
        coEvery { repositorio.favoritos(7) } returns ResultadoCatalogo.Exito(listOf(hojaldre))
        vm.cargar(1, 7)
        assertFalse(vm.esFavorito.value)
    }

    @Test
    fun cargar_sin_usuario_no_consulta_los_favoritos() {
        coEvery { repositorio.buscarPorId(1) } returns ResultadoCatalogo.Exito(sables)
        vm.cargar(1, 0)
        assertEquals(sables, vm.contenido.value)
        assertFalse(vm.esFavorito.value)
        coVerify(exactly = 0) { repositorio.favoritos(any()) }
    }

    @Test
    fun cargar_si_falla_la_lista_de_favoritos_no_marca_favorito() {
        coEvery { repositorio.buscarPorId(1) } returns ResultadoCatalogo.Exito(sables)
        coEvery { repositorio.favoritos(7) } returns ResultadoCatalogo.ErrorRed
        vm.cargar(1, 7)
        assertEquals(sables, vm.contenido.value)
        assertFalse(vm.esFavorito.value)
    }

    @Test
    fun cargar_contenido_inexistente_muestra_No_encontrado() {
        coEvery { repositorio.buscarPorId(99) } returns ResultadoCatalogo.NoEncontrado
        vm.cargar(99, 7)
        assertNull(vm.contenido.value)
        assertEquals(ResultadoCatalogo.NoEncontrado.mensajeError(), vm.error.value)
        assertFalse(vm.cargando.value)
    }

    @Test
    fun cargar_sin_internet_muestra_el_mensaje_de_error() {
        coEvery { repositorio.buscarPorId(1) } returns ResultadoCatalogo.ErrorRed
        vm.cargar(1, 7)
        assertNull(vm.contenido.value)
        assertEquals(ResultadoCatalogo.ErrorRed.mensajeError(), vm.error.value)
    }

    @Test
    fun reintentar_despues_de_un_error_limpia_el_error() {
        coEvery { repositorio.buscarPorId(1) } returns ResultadoCatalogo.ErrorRed
        vm.cargar(1, 7)
        coEvery { repositorio.buscarPorId(1) } returns ResultadoCatalogo.Exito(sables)
        coEvery { repositorio.favoritos(7) } returns ResultadoCatalogo.Exito(emptyList())
        vm.cargar(1, 7)
        assertNull(vm.error.value)
        assertEquals(sables, vm.contenido.value)
    }

    @Test
    fun guardar_favorito_actualiza_el_estado_y_avisa() {
        coEvery { repositorio.buscarPorId(1) } returns ResultadoCatalogo.Exito(sables)
        coEvery { repositorio.favoritos(7) } returns ResultadoCatalogo.Exito(emptyList())
        coEvery { repositorio.cambiarFavorito(7, 1, true) } returns ResultadoCatalogo.Exito(Unit)
        vm.cargar(1, 7)
        vm.cambiarFavorito()
        assertTrue(vm.esFavorito.value)
        assertEquals("Guardado en tus favoritos", vm.mensaje.value)
        coVerify(exactly = 1) { repositorio.cambiarFavorito(7, 1, true) }
    }

    @Test
    fun quitar_favorito_actualiza_el_estado_y_avisa() {
        coEvery { repositorio.buscarPorId(1) } returns ResultadoCatalogo.Exito(sables)
        coEvery { repositorio.favoritos(7) } returns ResultadoCatalogo.Exito(listOf(sables))
        coEvery { repositorio.cambiarFavorito(7, 1, false) } returns ResultadoCatalogo.Exito(Unit)
        vm.cargar(1, 7)
        vm.cambiarFavorito()
        assertFalse(vm.esFavorito.value)
        assertEquals("Quitado de tus favoritos", vm.mensaje.value)
        coVerify(exactly = 1) { repositorio.cambiarFavorito(7, 1, false) }
    }

    @Test
    fun guardar_y_quitar_seguidos_vuelve_al_estado_inicial() {
        coEvery { repositorio.buscarPorId(1) } returns ResultadoCatalogo.Exito(sables)
        coEvery { repositorio.favoritos(7) } returns ResultadoCatalogo.Exito(emptyList())
        coEvery { repositorio.cambiarFavorito(any(), any(), any()) } returns ResultadoCatalogo.Exito(Unit)
        vm.cargar(1, 7)
        vm.cambiarFavorito()
        vm.cambiarFavorito()
        assertFalse(vm.esFavorito.value)
        coVerify(exactly = 1) { repositorio.cambiarFavorito(7, 1, true) }
        coVerify(exactly = 1) { repositorio.cambiarFavorito(7, 1, false) }
    }

    @Test
    fun si_falla_la_red_al_guardar_no_cambia_el_favorito_y_muestra_el_error() {
        coEvery { repositorio.buscarPorId(1) } returns ResultadoCatalogo.Exito(sables)
        coEvery { repositorio.favoritos(7) } returns ResultadoCatalogo.Exito(emptyList())
        coEvery { repositorio.cambiarFavorito(any(), any(), any()) } returns ResultadoCatalogo.ErrorRed
        vm.cargar(1, 7)
        vm.cambiarFavorito()
        assertFalse(vm.esFavorito.value)
        assertEquals(ResultadoCatalogo.ErrorRed.mensajeError(), vm.mensaje.value)
    }

    @Test
    fun si_falla_el_servidor_al_quitar_el_favorito_sigue_guardado() {
        coEvery { repositorio.buscarPorId(1) } returns ResultadoCatalogo.Exito(sables)
        coEvery { repositorio.favoritos(7) } returns ResultadoCatalogo.Exito(listOf(sables))
        coEvery { repositorio.cambiarFavorito(any(), any(), any()) } returns ResultadoCatalogo.ErrorServidor
        vm.cargar(1, 7)
        vm.cambiarFavorito()
        assertTrue(vm.esFavorito.value)
        assertEquals(ResultadoCatalogo.ErrorServidor.mensajeError(), vm.mensaje.value)
    }

    @Test
    fun mensaje_mostrado_limpia_el_mensaje() {
        coEvery { repositorio.buscarPorId(1) } returns ResultadoCatalogo.Exito(sables)
        coEvery { repositorio.favoritos(7) } returns ResultadoCatalogo.Exito(emptyList())
        coEvery { repositorio.cambiarFavorito(any(), any(), any()) } returns ResultadoCatalogo.Exito(Unit)
        vm.cargar(1, 7)
        vm.cambiarFavorito()
        vm.mensajeMostrado()
        assertNull(vm.mensaje.value)
    }

    @Test
    fun cambiar_favorito_sin_cargar_antes_no_llama_al_servidor() {
        vm.cambiarFavorito()
        assertFalse(vm.esFavorito.value)
        coVerify(exactly = 0) { repositorio.cambiarFavorito(any(), any(), any()) }
    }

    @Test
    fun cambiar_favorito_sin_usuario_no_llama_al_servidor() {
        coEvery { repositorio.buscarPorId(1) } returns ResultadoCatalogo.Exito(sables)
        vm.cargar(1, 0)
        vm.cambiarFavorito()
        assertNull(vm.mensaje.value)
        coVerify(exactly = 0) { repositorio.cambiarFavorito(any(), any(), any()) }
    }
}
