package com.example.appmm_grupo5.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.appmm_grupo5.navigation.Screen
import com.example.appmm_grupo5.ui.utils.obtenerWindowSizeClass
import kotlinx.coroutines.launch

// Opcion de menu lateral
private data class ItemMenu(val screen: Screen, val etiqueta: String, val icono: ImageVector)

private val itemsMenu = listOf(
    ItemMenu(Screen.Inicio, "Inicio", Icons.Filled.Home),
    ItemMenu(Screen.Aprender, "Aprender", Icons.AutoMirrored.Filled.MenuBook),
    ItemMenu(Screen.Eventos, "Eventos", Icons.Filled.Event),
    ItemMenu(Screen.Perfil, "Perfil", Icons.Filled.Person)
)

// Se adapta a la pantalla segun el tamaño
@Composable
fun HomeScreenAdaptable(
    onComenzar: () -> Unit = {},
    onVerEventos: () -> Unit = {},
    onIrA: (Screen) -> Unit = {}
) {
    val windowSizeClass = obtenerWindowSizeClass()

    // Estado del drawer(abierto o cerrado) y un scope de corrutina para abrirlo o cerrarlo
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "Menú",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(16.dp)
                )
                itemsMenu.forEach { item ->
                    NavigationDrawerItem(
                        label = { Text(item.etiqueta) },
                        icon = { Icon(item.icono, contentDescription = null) },
                        // Estamos en Inicio, asi que esa opcion aparece seleccionada
                        selected = item.screen == Screen.Inicio,
                        onClick = {
                            scope.launch { drawerState.close() }
                            onIrA(item.screen)
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ) {
        // El icono de menu de la barra superior abre el drawer
        val abrirMenu: () -> Unit = { scope.launch { drawerState.open() } }

        when (windowSizeClass.widthSizeClass) {
            WindowWidthSizeClass.Compact -> HomeScreenCompacta(onComenzar, onVerEventos, abrirMenu)
            WindowWidthSizeClass.Medium -> HomeScreenMediana(onComenzar, onVerEventos, abrirMenu)
            WindowWidthSizeClass.Expanded -> HomeScreenExpandida(onComenzar, onVerEventos, abrirMenu)
        }
    }
}