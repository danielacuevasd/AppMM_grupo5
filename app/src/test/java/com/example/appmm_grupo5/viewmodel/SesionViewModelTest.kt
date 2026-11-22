package com.example.appmm_grupo5.viewmodel

import android.app.Application
import android.net.Uri
import com.example.appmm_grupo5.data.AppDatabase
import com.example.appmm_grupo5.data.EncuestaDao
import com.example.appmm_grupo5.data.PerfilNegocioDao
import com.example.appmm_grupo5.data.SesionDataStore
import com.example.appmm_grupo5.model.Encuesta
import com.example.appmm_grupo5.model.EncuestaUiState
import com.example.appmm_grupo5.model.PerfilNegocio
import com.example.appmm_grupo5.model.PerfilUiState
import com.example.appmm_grupo5.model.Usuario
import com.example.appmm_grupo5.repository.ResultadoUsuario
import com.example.appmm_grupo5.repository.UsuarioRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkObject
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test
import java.nio.file.Files
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

// Se prueba la lógica de sesión sin teléfono: DataStore, Room y el servidor están mockeados
@OptIn(ExperimentalCoroutinesApi::class)
class SesionViewModelTest {

    private lateinit var repositorio: UsuarioRepository
    private lateinit var perfilDao: PerfilNegocioDao
    private lateinit var encuestaDao: EncuestaDao
    private val aplicacion = mockk<Application>(relaxed = true)
    private val usuario = Usuario(1L, "Camila", "camila@negocio.cl", "+56912345678")

    @Before
    fun preparar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        repositorio = mockk()
        perfilDao = mockk(relaxed = true)
        encuestaDao = mockk(relaxed = true)

        // Base de datos y DataStore falsos (el ViewModel los crea por dentro)
        val baseDeDatos = mockk<AppDatabase>()
        every { baseDeDatos.perfilNegocioDao() } returns perfilDao
        every { baseDeDatos.encuestaDao() } returns encuestaDao
        mockkObject(AppDatabase.Companion)
        every { AppDatabase.obtener(any()) } returns baseDeDatos
        mockkConstructor(SesionDataStore::class)
        sinSesionGuardada()
        coEvery { anyConstructed<SesionDataStore>().guardarSesion(any(), any()) } just Runs
        coEvery { anyConstructed<SesionDataStore>().cerrarSesion() } just Runs
    }

    @After
    fun limpiar() {
        unmockkAll()
        Dispatchers.resetMain()
    }

    private fun sinSesionGuardada() {
        every { anyConstructed<SesionDataStore>().obtenerSesion() } returns flowOf(false)
        every { anyConstructed<SesionDataStore>().obtenerUsuarioId() } returns flowOf(null)
        every { anyConstructed<SesionDataStore>().obtenerCorreo() } returns flowOf(null)
    }

    private fun conSesionGuardada(id: Long, correo: String) {
        every { anyConstructed<SesionDataStore>().obtenerSesion() } returns flowOf(true)
        every { anyConstructed<SesionDataStore>().obtenerUsuarioId() } returns flowOf(id)
        every { anyConstructed<SesionDataStore>().obtenerCorreo() } returns flowOf(correo)
    }

    // Crea el ViewModel con una sesión iniciada (usuario 1)
    private fun viewModelConSesion(): SesionViewModel {
        coEvery { repositorio.iniciarSesion(any(), any()) } returns ResultadoUsuario.Exito(usuario)
        val vm = SesionViewModel(aplicacion, repositorio)
        vm.ingresar("camila@negocio.cl", "clave1234") { }
        return vm
    }

    @Test
    fun sin_sesion_guardada_la_sesion_queda_inactiva() {
        val vm = SesionViewModel(aplicacion, repositorio)
        assertEquals(false, vm.sesionActiva.value)
        assertNull(vm.usuarioId.value)
    }

    @Test
    fun con_sesion_guardada_y_usuario_existente_se_mantiene_la_sesion() {
        conSesionGuardada(1L, "camila@negocio.cl")
        coEvery { repositorio.buscarPorId(1L) } returns ResultadoUsuario.Exito(usuario)

        val vm = SesionViewModel(aplicacion, repositorio)

        assertEquals(true, vm.sesionActiva.value)
        assertEquals(1L, vm.usuarioId.value)
        assertEquals("camila@negocio.cl", vm.correo.value)
        assertEquals(usuario, vm.usuarioActual.value)
    }

    @Test
    fun si_el_servidor_dice_que_el_usuario_no_existe_se_cierra_la_sesion() {
        conSesionGuardada(7L, "borrado@negocio.cl")
        coEvery { repositorio.buscarPorId(7L) } returns ResultadoUsuario.NoEncontrado

        val vm = SesionViewModel(aplicacion, repositorio)

        assertEquals(false, vm.sesionActiva.value)
        assertNull(vm.usuarioId.value)
        coVerify { anyConstructed<SesionDataStore>().cerrarSesion() }
    }

    @Test
    fun sin_conexion_al_iniciar_la_sesion_guardada_se_mantiene() {
        conSesionGuardada(1L, "camila@negocio.cl")
        coEvery { repositorio.buscarPorId(1L) } returns ResultadoUsuario.ErrorRed

        val vm = SesionViewModel(aplicacion, repositorio)

        assertEquals(true, vm.sesionActiva.value)
        assertEquals(1L, vm.usuarioId.value)
    }

    @Test
    fun ingresar_correcto_inicia_la_sesion() {
        coEvery { repositorio.iniciarSesion("camila@negocio.cl", "clave1234") } returns
                ResultadoUsuario.Exito(usuario)
        val vm = SesionViewModel(aplicacion, repositorio)

        var recibido: ResultadoUsuario? = null
        vm.ingresar("camila@negocio.cl", "clave1234") { recibido = it }

        assertEquals(ResultadoUsuario.Exito(usuario), recibido)
        assertEquals(true, vm.sesionActiva.value)
        assertEquals(1L, vm.usuarioId.value)
        coVerify { anyConstructed<SesionDataStore>().guardarSesion(1L, "camila@negocio.cl") }
    }

    @Test
    fun ingresar_con_clave_incorrecta_no_inicia_la_sesion() {
        coEvery { repositorio.iniciarSesion(any(), any()) } returns ResultadoUsuario.CredencialesIncorrectas
        val vm = SesionViewModel(aplicacion, repositorio)

        var recibido: ResultadoUsuario? = null
        vm.ingresar("camila@negocio.cl", "mala") { recibido = it }

        assertEquals(ResultadoUsuario.CredencialesIncorrectas, recibido)
        assertEquals(false, vm.sesionActiva.value)
        assertNull(vm.usuarioId.value)
    }

    @Test
    fun registrar_con_correo_repetido_no_inicia_la_sesion() {
        coEvery { repositorio.registrar(any(), any(), any(), any()) } returns ResultadoUsuario.CorreoRepetido
        val vm = SesionViewModel(aplicacion, repositorio)

        var recibido: ResultadoUsuario? = null
        vm.registrar("Camila", "camila@negocio.cl", "+56912345678", "clave1234") { recibido = it }

        assertEquals(ResultadoUsuario.CorreoRepetido, recibido)
        assertEquals(false, vm.sesionActiva.value)
    }

    @Test
    fun registrar_correcto_inicia_la_sesion() {
        coEvery { repositorio.registrar(any(), any(), any(), any()) } returns ResultadoUsuario.Exito(usuario)
        val vm = SesionViewModel(aplicacion, repositorio)

        vm.registrar("Camila", "camila@negocio.cl", "+56912345678", "clave1234") { }

        assertEquals(true, vm.sesionActiva.value)
        assertEquals(1L, vm.usuarioId.value)
    }

    @Test
    fun cerrar_sesion_limpia_el_estado() {
        val vm = viewModelConSesion()

        vm.cerrarSesion()

        assertEquals(false, vm.sesionActiva.value)
        assertNull(vm.usuarioId.value)
        assertNull(vm.correo.value)
        assertNull(vm.usuarioActual.value)
    }

    @Test
    fun actualizar_cuenta_sin_sesion_devuelve_NoEncontrado() {
        val vm = SesionViewModel(aplicacion, repositorio)

        var recibido: ResultadoUsuario? = null
        vm.actualizarCuenta("Camila", "+56912345678", null) { recibido = it }

        assertEquals(ResultadoUsuario.NoEncontrado, recibido)
    }

    @Test
    fun actualizar_cuenta_correcto_guarda_los_datos_nuevos() {
        val nuevo = usuario.copy(nombre = "Camila Nueva")
        coEvery { repositorio.actualizar(1L, "Camila Nueva", "+56912345678", null) } returns
                ResultadoUsuario.Exito(nuevo)
        val vm = viewModelConSesion()

        vm.actualizarCuenta("Camila Nueva", "+56912345678", null) { }

        assertEquals("Camila Nueva", vm.usuarioActual.value?.nombre)
    }

    @Test
    fun eliminar_cuenta_sin_sesion_devuelve_NoEncontrado() {
        val vm = SesionViewModel(aplicacion, repositorio)

        var recibido: ResultadoUsuario? = null
        vm.eliminarCuenta { recibido = it }

        assertEquals(ResultadoUsuario.NoEncontrado, recibido)
    }

    @Test
    fun eliminar_cuenta_correcto_cierra_la_sesion() {
        coEvery { repositorio.eliminar(1L) } returns ResultadoUsuario.Eliminado
        val vm = viewModelConSesion()

        var recibido: ResultadoUsuario? = null
        vm.eliminarCuenta { recibido = it }

        assertEquals(ResultadoUsuario.Eliminado, recibido)
        assertEquals(false, vm.sesionActiva.value)
    }

    @Test
    fun eliminar_cuenta_sin_conexion_mantiene_la_sesion() {
        coEvery { repositorio.eliminar(1L) } returns ResultadoUsuario.ErrorRed
        val vm = viewModelConSesion()

        vm.eliminarCuenta { }

        assertEquals(true, vm.sesionActiva.value)
    }

    // ---------- Perfil, encuesta y fotos ----------

    @Test
    fun guardar_perfil_con_sesion_lo_guarda_para_ese_usuario() {
        val vm = viewModelConSesion()
        val perfil = PerfilUiState(
            nombreNegocio = "Dulce Sur",
            rutEmpresa = "76.123.456-7",
            tipoNegocio = "Pastelería",
            empleados = "3",
            direccion = "Calle 1 123"
        )

        vm.guardarPerfil(perfil)

        coVerify {
            perfilDao.guardar(match { it.usuarioId == 1L && it.nombreNegocio == "Dulce Sur" && it.empleados == 3 })
        }
    }

    @Test
    fun guardar_perfil_sin_sesion_no_guarda_nada() {
        val vm = SesionViewModel(aplicacion, repositorio)

        vm.guardarPerfil(PerfilUiState(nombreNegocio = "Dulce Sur", empleados = "3"))

        coVerify(exactly = 0) { perfilDao.guardar(any()) }
    }

    @Test
    fun guardar_encuesta_con_sesion_la_guarda_para_ese_usuario() {
        val vm = viewModelConSesion()

        vm.guardarEncuesta(EncuestaUiState(intereses = setOf("Galletas", "Horneo"), objetivo = "Crecer"))

        coVerify { encuestaDao.guardar(match { it.usuarioId == 1L && it.intereses == "Galletas,Horneo" }) }
    }

    @Test
    fun guardar_encuesta_sin_sesion_no_guarda_nada() {
        val vm = SesionViewModel(aplicacion, repositorio)

        vm.guardarEncuesta(EncuestaUiState(intereses = setOf("Galletas"), objetivo = "Crecer"))

        coVerify(exactly = 0) { encuestaDao.guardar(any()) }
    }

    @Test
    fun cargar_datos_usuario_devuelve_perfil_y_encuesta_guardados() {
        coEvery { perfilDao.obtener(1L) } returns
                PerfilNegocio(1L, "Dulce Sur", "76.123.456-7", "Pastelería", 3, "Calle 1 123")
        coEvery { encuestaDao.obtener(1L) } returns Encuesta(1L, "Galletas,Horneo", "Crecer", true)
        val vm = SesionViewModel(aplicacion, repositorio)

        var perfilRecibido: PerfilUiState? = null
        var encuestaRecibida: EncuestaUiState? = null
        vm.cargarDatosUsuario(1L) { perfil, encuesta ->
            perfilRecibido = perfil
            encuestaRecibida = encuesta
        }

        assertEquals("Dulce Sur", perfilRecibido?.nombreNegocio)
        assertEquals("3", perfilRecibido?.empleados)
        assertEquals(setOf("Galletas", "Horneo"), encuestaRecibida?.intereses)
    }

    @Test
    fun cargar_datos_usuario_sin_datos_guardados_devuelve_null() {
        coEvery { perfilDao.obtener(any()) } returns null
        coEvery { encuestaDao.obtener(any()) } returns null
        val vm = SesionViewModel(aplicacion, repositorio)

        var perfilRecibido: PerfilUiState? = PerfilUiState()
        var encuestaRecibida: EncuestaUiState? = EncuestaUiState()
        vm.cargarDatosUsuario(9L) { perfil, encuesta ->
            perfilRecibido = perfil
            encuestaRecibida = encuesta
        }

        assertNull(perfilRecibido)
        assertNull(encuestaRecibida)
    }

    @Test
    fun guardar_foto_sin_sesion_devuelve_la_misma_imagen() {
        val vm = SesionViewModel(aplicacion, repositorio)
        val origen = mockk<Uri>()

        var recibida: Uri? = null
        vm.guardarFotoPerfil(origen) { recibida = it }

        assertSame(origen, recibida)
    }

    @Test
    fun cargar_foto_cuando_no_hay_ninguna_devuelve_null() {
        // Carpeta temporal vacía: no existe la subcarpeta "fotos"
        every { aplicacion.filesDir } returns Files.createTempDirectory("sesion_test").toFile()
        val vm = SesionViewModel(aplicacion, repositorio)

        // La búsqueda de archivos corre en otro hilo, por eso se espera el resultado
        val espera = CountDownLatch(1)
        var foto: Uri? = mockk()
        vm.cargarFotoPerfil(1L) {
            foto = it
            espera.countDown()
        }
        espera.await(5, TimeUnit.SECONDS)

        assertNull(foto)
    }
}