package com.example.appmm_grupo5.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appmm_grupo5.data.DatosEjemplo
import com.example.appmm_grupo5.model.Contenido
import com.example.appmm_grupo5.model.TipoContenido
import com.example.appmm_grupo5.ui.components.BotonPrincipal
import com.example.appmm_grupo5.ui.theme.AppMM_grupo5Theme
import com.example.appmm_grupo5.viewmodel.DetalleViewModel
import kotlinx.coroutines.launch

// Pantalla de detalle de un contenido, con la opción de guardarlo en favoritos.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleScreen(
    contenidoId: Long?,
    usuarioId: Long?,
    onVolver: () -> Unit,
    detalleViewModel: DetalleViewModel = viewModel()
) {
    val contenido by detalleViewModel.contenido.collectAsState()
    val esFavorito by detalleViewModel.esFavorito.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val puedeGuardar = usuarioId != null && usuarioId > 0L

    LaunchedEffect(contenidoId, usuarioId) {
        if (contenidoId != null) detalleViewModel.cargar(contenidoId, usuarioId ?: 0L)
    }

    // Cambia el favorito y avisa con un mensaje
    val alternarFavorito: () -> Unit = {
        detalleViewModel.cambiarFavorito()
        scope.launch {
            snackbarHostState.showSnackbar(
                if (esFavorito) "Quitado de tus favoritos" else "Guardado en tus favoritos"
            )
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detalle") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (puedeGuardar) {
                        IconButton(onClick = alternarFavorito) {
                            Icon(
                                imageVector = if (esFavorito) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = if (esFavorito) "Quitar de favoritos" else "Guardar en favoritos"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        val actual = contenido
        if (actual == null) {
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            DetalleContenido(
                contenido = actual,
                esFavorito = esFavorito,
                puedeGuardar = puedeGuardar,
                onAlternarFavorito = alternarFavorito,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

// Parte sin estado propio: solo dibuja lo que recibe
@Composable
fun DetalleContenido(
    contenido: Contenido,
    esFavorito: Boolean,
    puedeGuardar: Boolean,
    onAlternarFavorito: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Cabecera con el ícono del tipo de contenido
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = iconoDelTipo(contenido.tipo),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(72.dp)
            )
        }

        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "${contenido.tipo.uppercase()} · ${contenido.categoria.uppercase()}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(text = contenido.titulo, style = MaterialTheme.typography.headlineMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                DatoConIcono(icono = Icons.Filled.Schedule, texto = "${contenido.duracionMinutos} min")
                DatoConIcono(icono = Icons.Filled.BarChart, texto = contenido.nivel)
            }
            Text(text = contenido.descripcion, style = MaterialTheme.typography.bodyLarge)

            if (puedeGuardar) {
                BotonPrincipal(
                    texto = if (esFavorito) "Quitar de mis favoritos" else "Guardar en mis favoritos",
                    onClick = onAlternarFavorito
                )
            }
        }
    }
}

@Composable
private fun DatoConIcono(icono: ImageVector, texto: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
        Text(text = texto, style = MaterialTheme.typography.bodyLarge)
    }
}

private fun iconoDelTipo(tipo: String): ImageVector = when (tipo) {
    TipoContenido.RECETA -> Icons.Filled.Restaurant
    TipoContenido.CURSO -> Icons.Filled.School
    else -> Icons.Filled.PlayCircle
}

@Preview(showBackground = true)
@Composable
fun DetalleContenidoPreview() {
    AppMM_grupo5Theme {
        DetalleContenido(
            contenido = DatosEjemplo.contenidos.first().copy(id = 1),
            esFavorito = false,
            puedeGuardar = true,
            onAlternarFavorito = {}
        )
    }
}