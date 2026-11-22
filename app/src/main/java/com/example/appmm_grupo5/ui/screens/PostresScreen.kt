package com.example.appmm_grupo5.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.appmm_grupo5.model.Postre
import com.example.appmm_grupo5.navigation.Screen
import com.example.appmm_grupo5.ui.components.BarraNavegacion
import com.example.appmm_grupo5.viewmodel.MainViewModel
import com.example.appmm_grupo5.viewmodel.PostresViewModel

// Pantalla "Ideas de postres": datos de la API externa TheMealDB
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostresScreen(
    viewModel: MainViewModel,
    postresViewModel: PostresViewModel
) {
    val estado by postresViewModel.estado.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Ideas de postres") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            BarraNavegacion(
                seleccionada = Screen.Postres,
                onIrA = { destino -> viewModel.navigateTo(destino, singleTop = true) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = estado.busqueda,
                onValueChange = postresViewModel::onBusquedaChange,
                label = { Text("Buscar postre") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            when {
                estado.cargando -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                estado.error != null -> Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(estado.error ?: "", style = MaterialTheme.typography.bodyLarge)
                    Button(
                        onClick = postresViewModel::cargarPostres,
                        modifier = Modifier.padding(top = 12.dp)
                    ) { Text("Reintentar") }
                }
                estado.visibles.isEmpty() ->
                    Text("No encontramos postres con ese nombre", style = MaterialTheme.typography.bodyLarge)
                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(estado.visibles, key = { it.id }) { postre ->
                        TarjetaPostre(postre = postre, onClick = { postresViewModel.abrirDetalle(postre) })
                    }
                }
            }
        }
    }

    estado.detalle?.let { postre ->
        DetallePostre(
            postre = postre,
            cargando = estado.cargandoDetalle,
            error = estado.errorDetalle,
            onCerrar = postresViewModel::cerrarDetalle
        )
    }
}

@Composable
private fun TarjetaPostre(postre: Postre, onClick: () -> Unit) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .clickable { onClick() }) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = postre.imagen,
                contentDescription = postre.nombre,
                modifier = Modifier.size(80.dp),
                contentScale = ContentScale.Crop
            )
            Text(
                text = postre.nombre,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 12.dp)
            )
        }
    }
}

@Composable
private fun DetallePostre(
    postre: Postre,
    cargando: Boolean,
    error: String?,
    onCerrar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text(postre.nombre) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AsyncImage(
                    model = postre.imagen,
                    contentDescription = postre.nombre,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Crop
                )
                if (postre.categoria != null) Text("Categoría: ${postre.categoria}")
                if (postre.area != null) Text("Origen: ${postre.area}")
                if (cargando) CircularProgressIndicator()
                if (error != null) Text(error, color = MaterialTheme.colorScheme.error)
                if (postre.instrucciones != null) {
                    Text("Instrucciones", style = MaterialTheme.typography.titleSmall)
                    Text(postre.instrucciones)
                }
            }
        },
        confirmButton = { TextButton(onClick = onCerrar) { Text("Cerrar") } }
    )
}