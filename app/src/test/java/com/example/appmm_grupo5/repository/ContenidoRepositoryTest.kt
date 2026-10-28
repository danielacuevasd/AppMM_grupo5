package com.example.appmm_grupo5.repository

import com.example.appmm_grupo5.model.Contenido
import com.example.appmm_grupo5.model.TipoContenido
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ContenidoRepositoryTest {

    private val contenidos = listOf(
        Contenido(1, TipoContenido.RECETA, "Sablés de cacao", "Galletas", "Galletas de cacao", 60, "Básico"),
        Contenido(2, TipoContenido.CURSO, "Templado de chocolate", "Chocolatería", "Curso de templado", 40, "Intermedio"),
        Contenido(3, TipoContenido.VIDEO, "Plegado de hojaldre", "Masas de hoja", "Video de plegado", 15, "Intermedio"),
        Contenido(4, TipoContenido.RECETA, "Mousse de chocolate", "Chocolatería", "Postre de chocolate", 45, "Básico")
    )

    private val repositorio = ContenidoRepository(ContenidoDaoTest(contenidos), FavoritoDaoTest(contenidos))

    @Test
    fun sin_filtros_devuelve_todo_el_catalogo() = runBlocking {
        assertEquals(4, repositorio.filtrar().first().size)
    }

    @Test
    fun filtra_por_categoria() = runBlocking {
        val resultado = repositorio.filtrar(categoria = "Chocolatería").first()

        assertEquals(2, resultado.size)
        assertTrue(resultado.all { it.categoria == "Chocolatería" })
    }

    @Test
    fun filtra_por_tipo_y_categoria_a_la_vez() = runBlocking {
        val resultado = repositorio.filtrar(tipo = TipoContenido.RECETA, categoria = "Chocolatería").first()

        assertEquals(1, resultado.size)
        assertEquals("Mousse de chocolate", resultado[0].titulo)
    }

    @Test
    fun busca_un_contenido_por_su_id() = runBlocking {
        assertEquals("Plegado de hojaldre", repositorio.buscarPorId(3)?.titulo)
        assertNull(repositorio.buscarPorId(99))
    }

    @Test
    fun marcar_un_favorito_lo_agrega_a_la_lista_del_usuario() = runBlocking {
        repositorio.cambiarFavorito(usuarioId = 1, contenidoId = 2, marcar = true)

        assertEquals(listOf(2L), repositorio.idsFavoritos(1).first())
        assertTrue(repositorio.esFavorito(1, 2).first())
        assertEquals("Templado de chocolate", repositorio.observarFavoritos(1).first()[0].titulo)
    }

    @Test
    fun desmarcar_un_favorito_lo_quita() = runBlocking {
        repositorio.cambiarFavorito(1, 2, marcar = true)
        repositorio.cambiarFavorito(1, 2, marcar = false)

        assertTrue(repositorio.idsFavoritos(1).first().isEmpty())
        assertFalse(repositorio.esFavorito(1, 2).first())
    }

    @Test
    fun los_favoritos_son_de_cada_usuario() = runBlocking {
        repositorio.cambiarFavorito(usuarioId = 1, contenidoId = 2, marcar = true)

        assertTrue(repositorio.idsFavoritos(2).first().isEmpty())
    }
}