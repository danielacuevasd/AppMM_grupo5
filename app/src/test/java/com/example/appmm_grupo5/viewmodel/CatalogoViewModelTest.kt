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
class CatalogoViewModelTest {

    private lateinit var repositorio: ContenidoRepository

    private val sables = Contenido(1, "Receta", "Sables de cacao", "Galletas", "Galleta de cacao", 45, "Basico")
    private val hojaldre = Contenido(2, "Video", "Plegado de hojaldre", "Masas de hoja", "Tecnica de plegado", 12, "Avanzado")
    private val templado = Contenido(3, "Curso", "Templado de chocolate", "Chocolateria", "Curso de templado", 90, "Intermedio")

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
    fun al_iniciar_carga_todos_los_contenidos() {
        coEvery { repositorio.filtrar(null, null) } returns ResultadoCatalogo.Exito(listOf(sables, hojaldre, templado))
        val vm = CatalogoViewModel(repositorio)
        assertEquals(3, vm.contenidos.value.size)
        assertFalse(vm.cargando.value)
        assertNull(vm.error.value)
    }

    @Test
    fun seleccionar_tipo_pide_al_servidor_solo_ese_tipo() {
        coEvery { repositorio.filtrar(null, null) } returns ResultadoCatalogo.Exito(listOf(sables, hojaldre))
        coEvery { repositorio.filtrar("Video", null) } returns ResultadoCatalogo.Exito(listOf(hojaldre))
        val vm = CatalogoViewModel(repositorio)
        vm.seleccionarTipo("Video")
        assertEquals("Video", vm.tipo.value)
        assertEquals(listOf(hojaldre), vm.contenidos.value)
        coVerify(exactly = 1) { repositorio.filtrar("Video", null) }
    }

    @Test
    fun seleccionar_categoria_combina_con_el_tipo() {
        coEvery { repositorio.filtrar(any(), any()) } returns ResultadoCatalogo.Exito(emptyList())
        coEvery { repositorio.filtrar("Receta", "Galletas") } returns ResultadoCatalogo.Exito(listOf(sables))
        val vm = CatalogoViewModel(repositorio)
        vm.seleccionarTipo("Receta")
        vm.seleccionarCategoria("Galletas")
        assertEquals(listOf(sables), vm.contenidos.value)
    }

    @Test
    fun quitar_el_filtro_vuelve_a_pedir_todos() {
        coEvery { repositorio.filtrar(any(), any()) } returns ResultadoCatalogo.Exito(listOf(sables, hojaldre))
        val vm = CatalogoViewModel(repositorio)
        vm.seleccionarTipo("Video")
        vm.seleccionarTipo(null)
        assertNull(vm.tipo.value)
        coVerify(exactly = 2) { repositorio.filtrar(null, null) }
    }

    @Test
    fun sin_internet_muestra_el_mensaje_y_vacia_la_lista() {
        coEvery { repositorio.filtrar(any(), any()) } returns ResultadoCatalogo.ErrorRed
        val vm = CatalogoViewModel(repositorio)
        assertEquals(ResultadoCatalogo.ErrorRed.mensajeError(), vm.error.value)
        assertTrue(vm.contenidos.value.isEmpty())
        assertFalse(vm.cargando.value)
    }

    @Test
    fun error_del_servidor_muestra_el_mensaje() {
        coEvery { repositorio.filtrar(any(), any()) } returns ResultadoCatalogo.ErrorServidor
        val vm = CatalogoViewModel(repositorio)
        assertEquals(ResultadoCatalogo.ErrorServidor.mensajeError(), vm.error.value)
    }

    @Test
    fun reintentar_despues_de_un_error_carga_la_lista_y_limpia_el_error() {
        coEvery { repositorio.filtrar(any(), any()) } returns ResultadoCatalogo.ErrorRed
        val vm = CatalogoViewModel(repositorio)
        coEvery { repositorio.filtrar(any(), any()) } returns ResultadoCatalogo.Exito(listOf(sables))
        vm.recargar()
        assertNull(vm.error.value)
        assertEquals(listOf(sables), vm.contenidos.value)
    }

    @Test
    fun guardados_pide_los_favoritos_del_usuario() {
        coEvery { repositorio.filtrar(any(), any()) } returns ResultadoCatalogo.Exito(listOf(sables, hojaldre))
        coEvery { repositorio.favoritos(7) } returns ResultadoCatalogo.Exito(listOf(hojaldre))
        val vm = CatalogoViewModel(repositorio)
        vm.fijarUsuario(7)
        vm.alternarSoloGuardados()
        assertTrue(vm.soloGuardados.value)
        assertEquals(listOf(hojaldre), vm.contenidos.value)
        coVerify(exactly = 1) { repositorio.favoritos(7) }
    }

    @Test
    fun guardados_aplica_los_filtros_de_tipo_y_categoria() {
        coEvery { repositorio.filtrar(any(), any()) } returns ResultadoCatalogo.Exito(emptyList())
        coEvery { repositorio.favoritos(7) } returns ResultadoCatalogo.Exito(listOf(sables, hojaldre, templado))
        val vm = CatalogoViewModel(repositorio)
        vm.fijarUsuario(7)
        vm.alternarSoloGuardados()
        vm.seleccionarTipo("Curso")
        assertEquals(listOf(templado), vm.contenidos.value)
        vm.seleccionarTipo(null)
        vm.seleccionarCategoria("Galletas")
        assertEquals(listOf(sables), vm.contenidos.value)
    }

    @Test
    fun guardados_sin_usuario_devuelve_lista_vacia_sin_llamar_al_servidor() {
        coEvery { repositorio.filtrar(any(), any()) } returns ResultadoCatalogo.Exito(listOf(sables))
        val vm = CatalogoViewModel(repositorio)
        vm.alternarSoloGuardados()
        assertTrue(vm.contenidos.value.isEmpty())
        assertNull(vm.error.value)
        coVerify(exactly = 0) { repositorio.favoritos(any()) }
    }

    @Test
    fun guardados_con_error_de_red_muestra_el_mensaje() {
        coEvery { repositorio.filtrar(any(), any()) } returns ResultadoCatalogo.Exito(listOf(sables))
        coEvery { repositorio.favoritos(any()) } returns ResultadoCatalogo.ErrorRed
        val vm = CatalogoViewModel(repositorio)
        vm.fijarUsuario(7)
        vm.alternarSoloGuardados()
        assertEquals(ResultadoCatalogo.ErrorRed.mensajeError(), vm.error.value)
        assertTrue(vm.contenidos.value.isEmpty())
    }

    @Test
    fun alternar_guardados_dos_veces_vuelve_al_catalogo_completo() {
        coEvery { repositorio.filtrar(any(), any()) } returns ResultadoCatalogo.Exito(listOf(sables, hojaldre))
        coEvery { repositorio.favoritos(any()) } returns ResultadoCatalogo.Exito(listOf(sables))
        val vm = CatalogoViewModel(repositorio)
        vm.fijarUsuario(7)
        vm.alternarSoloGuardados()
        vm.alternarSoloGuardados()
        assertFalse(vm.soloGuardados.value)
        assertEquals(2, vm.contenidos.value.size)
    }

    @Test
    fun cambiar_de_usuario_con_guardados_activo_recarga_la_lista() {
        coEvery { repositorio.filtrar(any(), any()) } returns ResultadoCatalogo.Exito(emptyList())
        coEvery { repositorio.favoritos(7) } returns ResultadoCatalogo.Exito(listOf(sables))
        coEvery { repositorio.favoritos(8) } returns ResultadoCatalogo.Exito(listOf(hojaldre))
        val vm = CatalogoViewModel(repositorio)
        vm.fijarUsuario(7)
        vm.alternarSoloGuardados()
        vm.fijarUsuario(8)
        assertEquals(listOf(hojaldre), vm.contenidos.value)
    }
}
