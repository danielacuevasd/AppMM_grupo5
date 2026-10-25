package com.example.appmm_grupo5.ui.screens

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import com.example.appmm_grupo5.ui.utils.obtenerWindowSizeClass

// Se asigna automaticamente al tamano de la pantalla
// Recibe la accion del boton y se las pasa al que corresponda.
@Composable
fun HomeScreenAdaptable(
    onComenzar: () -> Unit = {},
    onVerEventos: () -> Unit = {}
) {
    val windowSizeClass = obtenerWindowSizeClass()
    when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> HomeScreenCompacta(onComenzar, onVerEventos)
        WindowWidthSizeClass.Medium -> HomeScreenMediana(onComenzar, onVerEventos)
        WindowWidthSizeClass.Expanded -> HomeScreenExpandida(onComenzar, onVerEventos)
    }
}