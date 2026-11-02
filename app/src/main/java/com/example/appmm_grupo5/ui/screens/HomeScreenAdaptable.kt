package com.example.appmm_grupo5.ui.screens

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import com.example.appmm_grupo5.navigation.Screen
import com.example.appmm_grupo5.ui.utils.obtenerWindowSizeClass

// Se adapta a la pantalla segun el tamaño
@Composable
fun HomeScreenAdaptable(
    onComenzar: () -> Unit = {},
    onIrA: (Screen) -> Unit = {}
) {
    val windowSizeClass = obtenerWindowSizeClass()

    when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> HomeScreenCompacta(onComenzar, onIrA)
        WindowWidthSizeClass.Medium -> HomeScreenMediana(onComenzar, onIrA)
        WindowWidthSizeClass.Expanded -> HomeScreenExpandida(onComenzar, onIrA)
    }
}