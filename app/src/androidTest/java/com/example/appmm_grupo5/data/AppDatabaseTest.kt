package com.example.appmm_grupo5.data

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
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
}
