package com.example.appmm_grupo5.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.appmm_grupo5.data.DatosEjemplo
import com.example.appmm_grupo5.model.Contenido
import com.example.appmm_grupo5.model.OpcionesPerfil
import com.example.appmm_grupo5.model.TipoContenido
import com.example.appmm_grupo5.ui.theme.AppMM_grupo5Theme
import com.example.appmm_grupo5.viewmodel.CatalogoViewModel
import com.example.appmm_grupo5.viewmodel.MainViewModel

// Pantalla Aprender: catálogo de recetas, videos y cursos con filtros por tipo y categoría.
// onAbrirContenido recibe el id del contenido tocado (el detalle se conecta en la tarjeta 12).
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AprenderScreen(
    viewModel: MainViewModel,
    catalogoViewModel: CatalogoViewModel,
    onAbrirContenido: (Long) -> Unit = {}
) {
    val contenidos by catalogoViewModel.contenidos.collectAsState()
    val tipoElegido by catalogoViewModel.tipo.collectAsState()
    val categoriaElegida by catalogoViewModel.categoria.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Aprender") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
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
        AprenderContenido(
            contenidos = contenidos,
            tipoElegido = tipoElegido,
            categoriaElegida = categoriaElegida,
            onTipo = catalogoViewModel::seleccionarTipo,
            onCategoria = catalogoViewModel::seleccionarCategoria,
            onAbrirContenido = onAbrirContenido,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

// Parte sin estado propio: solo dibuja lo que recibe
@Composable
fun AprenderContenido(
    contenidos: List<Contenido>,
    tipoElegido: String?,
    categoriaElegida: String?,
    onTipo: (String?) -> Unit,
    onCategoria: (String?) -> Unit,
    onAbrirContenido: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        // Filtro por tipo: Todo, Recetas, Cursos, Videos
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 12.dp)
        ) {
            item {
                FilterChip(
                    selected = tipoElegido == null,
                    onClick = { onTipo(null) },
                    label = { Text("Todo") }
                )
            }
            items(TipoContenido.todos) { tipo ->
                FilterChip(
                    selected = tipoElegido == tipo,
                    onClick = { onTipo(if (tipoElegido == tipo) null else tipo) },
                    label = { Text("${tipo}s") }
                )
            }
        }

        // Filtro por categoría (las mismas de los intereses de la encuesta)
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            item {
                FilterChip(
                    selected = categoriaElegida == null,
                    onClick = { onCategoria(null) },
                    label = { Text("Todas") }
                )
            }
            items(OpcionesPerfil.intereses) { categoria ->
                FilterChip(
                    selected = categoriaElegida == categoria,
                    onClick = { onCategoria(if (categoriaElegida == categoria) null else categoria) },
                    label = { Text(categoria) }
                )
            }
        }

        if (contenidos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay contenidos con estos filtros",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(contenidos, key = { it.id }) { contenido ->
                    TarjetaContenido(
                        contenido = contenido,
                        onClick = { onAbrirContenido(contenido.id) },
                        modifier = Modifier.animateItem()
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TarjetaContenido(
    contenido: Contenido,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "${contenido.tipo.uppercase()} · ${contenido.categoria.uppercase()}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(text = contenido.titulo, style = MaterialTheme.typography.titleMedium)
            Text(
                text = "${contenido.duracionMinutos} min · ${contenido.nivel}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = contenido.descripcion,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AprenderContenidoPreview() {
    AppMM_grupo5Theme {
        AprenderContenido(
            // Se numeran los ids porque la lista necesita una clave única por elemento
            contenidos = DatosEjemplo.contenidos.mapIndexed { posicion, contenido ->
                contenido.copy(id = posicion + 1L)
            },
            tipoElegido = null,
            categoriaElegida = null,
            onTipo = {},
            onCategoria = {},
            onAbrirContenido = {}
        )
    }
}