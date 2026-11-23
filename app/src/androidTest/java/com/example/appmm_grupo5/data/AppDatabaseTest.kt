package com.example.appmm_grupo5.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.appmm_grupo5.model.Encuesta
import com.example.appmm_grupo5.model.PerfilNegocio
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
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

    private fun perfil(usuarioId: Long) =
        PerfilNegocio(usuarioId, "Taller Dulce Sur", "76.086.428-5", "Pastelería", 4, "Av. Los Aromos 123")

    @Test
    fun el_perfil_se_guarda_y_se_reemplaza() {
        runBlocking {
            db.perfilNegocioDao().guardar(perfil(1))
            db.perfilNegocioDao().guardar(perfil(1).copy(empleados = 12))
            assertEquals(12, db.perfilNegocioDao().obtener(1)?.empleados)
        }
    }

    @Test
    fun cada_usuario_tiene_su_propio_perfil() {
        runBlocking {
            db.perfilNegocioDao().guardar(perfil(1))
            db.perfilNegocioDao().guardar(perfil(2).copy(nombreNegocio = "Panadería Norte"))
            assertEquals("Taller Dulce Sur", db.perfilNegocioDao().obtener(1)?.nombreNegocio)
            assertEquals("Panadería Norte", db.perfilNegocioDao().obtener(2)?.nombreNegocio)
            assertNull(db.perfilNegocioDao().obtener(3))
        }
    }

    @Test
    fun la_encuesta_se_guarda_y_se_reemplaza() {
        runBlocking {
            val encuesta = Encuesta(1, "Galletas,Chocolatería", "Aprender técnicas", true)
            db.encuestaDao().guardar(encuesta)
            db.encuestaDao().guardar(encuesta.copy(recibeNovedades = false))
            val guardada = db.encuestaDao().obtener(1)
            assertEquals(false, guardada?.recibeNovedades)
            assertEquals(setOf("Galletas", "Chocolatería"), guardada?.listaIntereses())
        }
    }
}
