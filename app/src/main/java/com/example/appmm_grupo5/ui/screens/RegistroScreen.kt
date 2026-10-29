package com.example.appmm_grupo5.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.appmm_grupo5.ui.components.AceptarTerminos
import com.example.appmm_grupo5.ui.components.BotonPrincipal
import com.example.appmm_grupo5.ui.components.CampoTexto
import com.example.appmm_grupo5.ui.components.DialogoErrores
import com.example.appmm_grupo5.ui.components.MensajeExito
import com.example.appmm_grupo5.ui.theme.AppMM_grupo5Theme
import com.example.appmm_grupo5.viewmodel.UsuarioViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Pantalla de registro. Si hay errores muestra el diálogo;
// si todo está bien, muestra el check animado y avanza.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    viewModel: UsuarioViewModel,
    onRegistroExitoso: () -> Unit,
    onVolver: () -> Unit,
    onRegistrar: ((Boolean) -> Unit) -> Unit = { resultado -> resultado(true) }
) {
    val estado by viewModel.registro.collectAsState()
    var mostrarDialogo by remember { mutableStateOf(false) }
    var exito by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Crear cuenta") },
                    navigationIcon = {
                        IconButton(onClick = onVolver) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Cuéntanos lo esencial para recomendarte contenido útil para tu día a día.",
                    style = MaterialTheme.typography.bodyLarge
                )

                CampoTexto(
                    valor = estado.nombre,
                    onValorChange = viewModel::onNombreChange,
                    etiqueta = "Tu nombre",
                    iconoInicial = Icons.Filled.Person,
                    error = estado.errores.nombre
                )
                CampoTexto(
                    valor = estado.correo,
                    onValorChange = viewModel::onCorreoChange,
                    etiqueta = "Correo electrónico",
                    iconoInicial = Icons.Filled.Email,
                    tipoTeclado = KeyboardType.Email,
                    error = estado.errores.correo
                )
                CampoTexto(
                    valor = estado.telefono,
                    onValorChange = viewModel::onTelefonoChange,
                    etiqueta = "Celular",
                    iconoInicial = Icons.Filled.Phone,
                    tipoTeclado = KeyboardType.Phone,
                    error = estado.errores.telefono
                )
                CampoTexto(
                    valor = estado.clave,
                    onValorChange = viewModel::onClaveChange,
                    etiqueta = "Clave",
                    iconoInicial = Icons.Filled.Lock,
                    esClave = true,
                    error = estado.errores.clave
                )
                CampoTexto(
                    valor = estado.confirmarClave,
                    onValorChange = viewModel::onConfirmarClaveChange,
                    etiqueta = "Confirma tu clave",
                    iconoInicial = Icons.Filled.Lock,
                    esClave = true,
                    error = estado.errores.confirmarClave
                )
                AceptarTerminos(
                    aceptado = estado.aceptaTerminos,
                    onCambio = viewModel::onTerminosChange,
                    error = estado.errores.aceptaTerminos
                )

                BotonPrincipal(
                    texto = "Crear cuenta",
                    habilitado = !exito,
                    onClick = {
                        if (viewModel.validarRegistro()) {
                            onRegistrar { correcto ->
                                if (correcto) {
                                    exito = true
                                    scope.launch {
                                        delay(1500)
                                        onRegistroExitoso()
                                    }
                                } else {
                                    viewModel.correoYaRegistrado()
                                }
                            }
                        } else {
                            mostrarDialogo = true
                        }
                    }
                )
            }
        }

        MensajeExito(
            visible = exito,
            mensaje = "Cuenta creada correctamente",
            modifier = Modifier.fillMaxSize()
        )
    }

    if (mostrarDialogo) {
        DialogoErrores(
            errores = estado.errores.mensajes(),
            onCerrar = { mostrarDialogo = false }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RegistroScreenPreview() {
    AppMM_grupo5Theme {
        RegistroScreen(viewModel = UsuarioViewModel(), onRegistroExitoso = {}, onVolver = {})
    }
}