package com.example.appmm_grupo5.ui.screens

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import com.example.appmm_grupo5.ui.utils.obtenerWindowSizeClass

@Composable
fun HomeScreen2() {
    val windowSizeClass = obtenerWindowSizeClass()
    when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> HomeScreenCompacta()
        WindowWidthSizeClass.Medium -> HomeScreenMediana()
        WindowWidthSizeClass.Expanded -> HomeScreenExpandida()
    }
}