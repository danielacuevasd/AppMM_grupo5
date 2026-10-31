package com.example.appmm_grupo5.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.appmm_grupo5.ui.components.ImagenInteligente
import java.io.File
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.appmm_grupo5.model.OpcionesPerfil
import com.example.appmm_grupo5.model.PerfilUiState
import com.example.appmm_grupo5.navigation.Screen
import com.example.appmm_grupo5.ui.components.BotonPrincipal
import com.example.appmm_grupo5.ui.components.CampoTexto
import com.example.appmm_grupo5.ui.components.DialogoErrores
import com.example.appmm_grupo5.ui.components.OpcionesRadio
import com.example.appmm_grupo5.ui.theme.AppMM_grupo5Theme
import com.example.appmm_grupo5.viewmodel.MainViewModel
import com.example.appmm_grupo5.viewmodel.PerfilViewModel
import kotlinx.coroutines.launch

private data class ItemBarra(val screen: Screen, val etiqueta: String, val icono: ImageVector)

private val itemsBarra = listOf(
    ItemBarra(Screen.Inicio, "Inicio", Icons.Filled.Home),
    ItemBarra(Screen.Aprender, "Aprender", Icons.AutoMirrored.Filled.MenuBook),
    ItemBarra(Screen.Eventos, "Eventos", Icons.Filled.Event),
    ItemBarra(Screen.Perfil, "Perfil", Icons.Filled.Person)
)

// Pantalla de Perfil. No tiene reglas: pide al ViewModel que valide y reaccione al resultado
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    viewModel: MainViewModel,
    perfilViewModel: PerfilViewModel,
    correo: String? = null,
    onIrAEncuesta: () -> Unit = {},
    onCerrarSesion: () -> Unit = {},
    onGuardar: (PerfilUiState) -> Unit = {}
) {
    val perfil by perfilViewModel.perfil.collectAsState()
    var mostrarErrores by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val contexto = LocalContext.current
    val imagenUri by perfilViewModel.imagenUri.collectAsState()
    var uriCamara by rememberSaveable { mutableStateOf<Uri?>(null) }

    val galeria = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        perfilViewModel.onImagenDeGaleria(uri)
    }

    val camara = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { exito ->
        perfilViewModel.onImagenDeCamara(exito, uriCamara)
    }

    fun abrirCamara() {
        val carpeta = File(contexto.cacheDir, "fotos").apply { mkdirs() }
        val archivo = File(carpeta, "negocio_${System.currentTimeMillis()}.jpg")
        val uri = FileProvider.getUriForFile(contexto, "${contexto.packageName}.fileprovider", archivo)
        uriCamara = uri
        camara.launch(uri)
    }

    val permisoCamara = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
        if (concedido) {
            abrirCamara()
        } else {
            scope.launch {
                snackbarHostState.showSnackbar("Sin permiso de camara no se puede tomar la foto. Puedes elegir una de la galeria")
            }
        }
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Perfil del negocio") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar {
                itemsBarra.forEach { item ->
                    NavigationBarItem(
                        // Esta pantalla es el Perfil, así que ese botón aparece seleccionado
                        selected = item.screen == Screen.Perfil,
                        // singleTop evita apilar varias veces la misma pantalla
                        onClick = { viewModel.navigateTo(item.screen, singleTop = true) },
                        label = { Text(item.etiqueta) },
                        icon = { Icon(item.icono, contentDescription = item.etiqueta) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // En tablet el formulario no se estira: se limita el ancho y queda centrado
            Column(
                modifier = Modifier.widthIn(max = 600.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    ImagenInteligente(uri = imagenUri)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { galeria.launch("image/*") },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                    ) {
                        Icon(Icons.Filled.PhotoLibrary, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Galería")
                    }
                    OutlinedButton(
                        onClick = {
                            val permitido = ContextCompat.checkSelfPermission(
                                contexto,
                                Manifest.permission.CAMERA
                            ) == PackageManager.PERMISSION_GRANTED
                            if (permitido) abrirCamara() else permisoCamara.launch(Manifest.permission.CAMERA)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                    ) {
                        Icon(Icons.Filled.PhotoCamera, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cámara")
                    }
                }
                CampoTexto(
                    valor = perfil.nombreNegocio,
                    onValorChange = perfilViewModel::onNombreNegocioChange,
                    etiqueta = "Nombre del negocio",
                    iconoInicial = Icons.Filled.Store,
                    error = perfil.errores.nombreNegocio
                )
                if (correo != null) {
                    Text(
                        text = "Sesión iniciada como $correo",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                CampoTexto(
                    valor = perfil.rutEmpresa,
                    onValorChange = perfilViewModel::onRutEmpresaChange,
                    etiqueta = "RUT de la empresa",
                    iconoInicial = Icons.Filled.Badge,
                    error = perfil.errores.rutEmpresa
                )
                OpcionesRadio(
                    titulo = "Tipo de negocio",
                    opciones = OpcionesPerfil.tiposNegocio,
                    seleccionada = perfil.tipoNegocio,
                    onSeleccion = perfilViewModel::onTipoNegocioChange,
                    error = perfil.errores.tipoNegocio
                )
                CampoTexto(
                    valor = perfil.empleados,
                    onValorChange = perfilViewModel::onEmpleadosChange,
                    etiqueta = "Personas que trabajan en tu negocio",
                    iconoInicial = Icons.Filled.Groups,
                    tipoTeclado = KeyboardType.Number,
                    error = perfil.errores.empleados
                )
                // El tamaño se calcula solo con la cantidad de personas
                if (perfil.tamanoNegocio.isNotEmpty()) {
                    Text(
                        text = "Tamaño de tu negocio: ${perfil.tamanoNegocio}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                CampoTexto(
                    valor = perfil.direccion,
                    onValorChange = perfilViewModel::onDireccionChange,
                    etiqueta = "Dirección",
                    iconoInicial = Icons.Filled.LocationOn,
                    error = perfil.errores.direccion
                )

                Spacer(modifier = Modifier.height(8.dp))

                BotonPrincipal(
                    texto = "Guardar perfil",
                    onClick = {
                        if (perfilViewModel.validarPerfil()) {
                            onGuardar(perfilViewModel.perfil.value)
                            scope.launch { snackbarHostState.showSnackbar("Perfil guardado") }
                        } else {
                            mostrarErrores = true
                        }
                    }
                )

                OutlinedButton(
                    onClick = onIrAEncuesta,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text("Completar encuesta de intereses")
                }

                OutlinedButton(
                    onClick = onCerrarSesion,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text("Cerrar sesión")
                }
            }
        }
    }

    if (mostrarErrores) {
        DialogoErrores(
            errores = perfil.errores.mensajes(),
            onCerrar = { mostrarErrores = false }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PerfilScreenPreview() {
    AppMM_grupo5Theme {
        PerfilScreen(viewModel = MainViewModel(), perfilViewModel = PerfilViewModel())
    }
}