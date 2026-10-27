package com.example.appmm_grupo5.data

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.appmm_grupo5.model.Favorito
import com.example.appmm_grupo5.model.PerfilNegocio
import com.example.appmm_grupo5.model.TipoContenido
import com.example.appmm_grupo5.model.Usuario
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {

    private lateinit var db: AppDatabase

    @Before
    fun crearBase() {
        val contexto = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(contexto, AppDatabase::class.java).build()
    }

    @After
    fun cerrarBase() {
        db.close()
    }

    private fun usuario(correo: String) = Usuario(
        nombre = "Camila Rojas",
        correo = correo,
        telefono = "912345678",
        claveHash = Seguridad.sha256("clave1234")
    )

    @Test
    fun guarda_y_busca_usuario_por_correo() {
        runBlocking {
            val id = db.usuarioDao().insertar(usuario("camila@negocio.cl"))
            val encontrado = db.usuarioDao().buscarPorCorreo("camila@negocio.cl")
            assertEquals(id, encontrado?.id)
        }
    }

    @Test
    fun no_permite_correos_repetidos() {
        runBlocking {
            db.usuarioDao().insertar(usuario("camila@negocio.cl"))
            try {
                db.usuarioDao().insertar(usuario("camila@negocio.cl"))
                fail("Debió fallar por correo repetido")
            } catch (e: SQLiteConstraintException) {
                // Es lo esperado
            }
        }
    }

    @Test
    fun el_perfil_se_guarda_y_se_reemplaza() {
        runBlocking {
            val id = db.usuarioDao().insertar(usuario("camila@negocio.cl"))
            val perfil = PerfilNegocio(id, "Taller Dulce Sur", "76.086.428-5", "Pastelería", 4, "Av. Los Aromos 123")
            db.perfilNegocioDao().guardar(perfil)
            db.perfilNegocioDao().guardar(perfil.copy(empleados = 12))
            assertEquals(12, db.perfilNegocioDao().obtener(id)?.empleados)
        }
    }

    @Test
    fun carga_los_datos_de_ejemplo_solo_una_vez() {
        runBlocking {
            db.cargarDatosEjemploSiHaceFalta()
            db.cargarDatosEjemploSiHaceFalta()
            assertEquals(DatosEjemplo.contenidos.size, db.contenidoDao().contar())
        }
    }

    @Test
    fun filtra_los_contenidos_por_tipo_y_categoria() {
        runBlocking {
            db.cargarDatosEjemploSiHaceFalta()
            val recetas = db.contenidoDao().filtrar(TipoContenido.RECETA, null).first()
            assertTrue(recetas.isNotEmpty() && recetas.all { it.tipo == TipoContenido.RECETA })

            val galletas = db.contenidoDao().filtrar(null, "Galletas").first()
            assertTrue(galletas.isNotEmpty() && galletas.all { it.categoria == "Galletas" })

            val todos = db.contenidoDao().filtrar(null, null).first()
            assertEquals(DatosEjemplo.contenidos.size, todos.size)
        }
    }

    @Test
    fun agrega_y_quita_favoritos() {
        runBlocking {
            val usuarioId = db.usuarioDao().insertar(usuario("camila@negocio.cl"))
            db.cargarDatosEjemploSiHaceFalta()
            val contenido = db.contenidoDao().filtrar(null, null).first().first()

            db.favoritoDao().agregar(Favorito(usuarioId, contenido.id))
            assertTrue(db.favoritoDao().esFavorito(usuarioId, contenido.id).first())
            assertEquals(1, db.favoritoDao().observarFavoritos(usuarioId).first().size)

            db.favoritoDao().quitar(usuarioId, contenido.id)
            assertFalse(db.favoritoDao().esFavorito(usuarioId, contenido.id).first())
            assertNotNull(db.contenidoDao().buscarPorId(contenido.id))
        }
    }
}