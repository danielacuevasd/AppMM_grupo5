package com.example.appmm_grupo5.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.appmm_grupo5.navigation.Screen
import com.example.appmm_grupo5.ui.theme.AppMM_grupo5Theme
import com.example.appmm_grupo5.viewmodel.MainViewModel
private data class ItemBarra(val screen: Screen, val etiqueta: String, val icono: ImageVector)

private val itemsBarra = listOf(
    ItemBarra(Screen.Inicio, "Inicio", Icons.Filled.Home),
    ItemBarra(Screen.Aprender, "Aprender", Icons.AutoMirrored.Filled.MenuBook),
    ItemBarra(Screen.Eventos, "Eventos", Icons.Filled.Event),
    ItemBarra(Screen.Perfil, "Perfil", Icons.Filled.Person)
)

// Pantalla de Perfil de usuario.
@Composable
fun PerfilScreen(viewModel: MainViewModel) {
    Scaffold(
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Aquí estara el perfil del usuario y de su negocio",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PerfilScreenPreview() {
    AppMM_grupo5Theme {
        PerfilScreen(viewModel = MainViewModel())
    }
}