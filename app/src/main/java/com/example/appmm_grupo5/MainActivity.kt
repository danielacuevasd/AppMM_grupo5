package com.example.appmm_grupo5

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.appmm_grupo5.ui.screens.HomeScreenAdaptable
import com.example.appmm_grupo5.ui.theme.AppMM_grupo5Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppMM_grupo5Theme {
                // Aqui llamamos la fun de HomeScreenAdaptable para que muestre el tamano correcto
                HomeScreenAdaptable()
            }
        }
    }
}