package com.example.appmm_grupo5.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.appmm_grupo5.navigation.Screen
import com.example.appmm_grupo5.ui.theme.AppMM_grupo5Theme

//Se cambia el codigo porque estamos trayendo la funcion de HomeScreen que ya tiene todo
@Composable
fun HomeScreenCompacta(
    onComenzar: () -> Unit = {},
    onIrA: (Screen) -> Unit = {}
) {
    HomeScreen(onComenzar = onComenzar, onIrA = onIrA)
}

@Preview(name = "Compacta", widthDp = 360, heightDp = 800, showBackground = true)
@Composable
fun HomeScreenCompactaPreview() {
    AppMM_grupo5Theme {
        HomeScreenCompacta()
    }
}