package com.example.appmm_grupo5.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.appmm_grupo5.model.EncuestaUiState
import com.example.appmm_grupo5.model.OpcionesPerfil
import com.example.appmm_grupo5.ui.components.BotonPrincipal
import com.example.appmm_grupo5.ui.components.DialogoErrores
import com.example.appmm_grupo5.ui.components.InterruptorConTexto
import com.example.appmm_grupo5.ui.components.OpcionesRadio
import com.example.appmm_grupo5.ui.theme.AppMM_grupo5Theme
import com.example.appmm_grupo5.viewmodel.PerfilViewModel
import kotlinx.coroutines.launch

// Encuesta de intereses con las categorias del cliente y objetivos.
// Validacion en PerfilViewModel.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EncuestaScreen(
    perfilViewModel: PerfilViewModel,
    onVolver: () -> Unit = {},
    onGuardar: (EncuestaUiState) -> Unit = {}
) {
    val encuesta by perfilViewModel.encuesta.collectAsState()
    var mostrarErrores by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Encuesta de intereses") },
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
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.widthIn(max = 600.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "¿Qué categoría te interesa aprender?",
                    style = MaterialTheme.typography.titleMedium
                )
                OpcionesInteres(
                    opciones = OpcionesPerfil.intereses,
                    marcados = encuesta.intereses,
                    onCambio = perfilViewModel::onInteresChange
                )
                encuesta.errores.intereses?.let { mensaje ->
                    Text(
                        text = mensaje,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OpcionesRadio(
                    titulo = "¿Cuáles son tus objetivos?",
                    opciones = OpcionesPerfil.objetivos,
                    seleccionada = encuesta.objetivo,
                    onSeleccion = perfilViewModel::onObjetivoChange,
                    error = encuesta.errores.objetivo
                )

                InterruptorConTexto(
                    titulo = "Recibir novedades",
                    descripcion = "Te avisaremos cuando se publique un nuevo video o curso",
                    activo = encuesta.recibeNovedades,
                    onCambio = perfilViewModel::onNovedadesChange
                )

                Spacer(modifier = Modifier.height(8.dp))

                BotonPrincipal(
                    texto = "Guardar encuesta",
                    onClick = {
                        if (perfilViewModel.validarEncuesta()) {
                            onGuardar(perfilViewModel.encuesta.value)
                            scope.launch { snackbarHostState.showSnackbar("Encuesta completada") }
                        } else {
                            mostrarErrores = true
                        }
                    }
                )
            }
        }
    }

    if (mostrarErrores) {
        DialogoErrores(
            errores = encuesta.errores.mensajes(),
            onCerrar = { mostrarErrores = false }
        )
    }
}

// Lista de casillas: cada fila se puede tocar completa para marcar o desmarcar
@Composable
private fun OpcionesInteres(
    opciones: List<String>,
    marcados: Set<String>,
    onCambio: (String, Boolean) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        opciones.forEach { opcion ->
            val marcado = opcion in marcados
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .toggleable(
                        value = marcado,
                        role = Role.Checkbox,
                        onValueChange = { onCambio(opcion, it) }
                    )
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(checked = marcado, onCheckedChange = null)
                Text(
                    text = opcion,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EncuestaScreenPreview() {
    AppMM_grupo5Theme {
        EncuestaScreen(perfilViewModel = PerfilViewModel())
    }
}