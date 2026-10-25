package com.example.appmm_grupo5

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.appmm_grupo5.navigation.AppNavGraph
import com.example.appmm_grupo5.ui.theme.AppMM_grupo5Theme
import com.example.appmm_grupo5.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppMM_grupo5Theme {
                // ViewModel de navegacion y controlador de navegacion
                val viewModel: MainViewModel = viewModel()
                val navController = rememberNavController()

                // Las pantallas vienen desde el graph
                AppNavGraph(navController = navController, viewModel = viewModel)
            }
        }
    }
}