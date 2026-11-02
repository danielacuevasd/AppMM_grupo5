package com.example.appmm_grupo5.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.appmm_grupo5.navigation.Screen

private data class ItemBarra(val screen: Screen, val etiqueta: String, val icono: ImageVector)

private val itemsBarra = listOf(
    ItemBarra(Screen.Inicio, "Inicio", Icons.Filled.Home),
    ItemBarra(Screen.Aprender, "Aprender", Icons.AutoMirrored.Filled.MenuBook),
    ItemBarra(Screen.Perfil, "Perfil", Icons.Filled.Person)
)

// Barra de navegacion inferior compartida por las pantallas principales.
@Composable
fun BarraNavegacion(
    seleccionada: Screen,
    onIrA: (Screen) -> Unit
) {
    NavigationBar {
        itemsBarra.forEach { item ->
            NavigationBarItem(
                selected = item.screen == seleccionada,
                onClick = { onIrA(item.screen) },
                label = { Text(item.etiqueta) },
                icon = { Icon(item.icono, contentDescription = item.etiqueta) }
            )
        }
    }
}