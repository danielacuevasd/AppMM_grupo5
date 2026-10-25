package com.example.appmm_grupo5.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.appmm_grupo5.ui.screens.AprenderScreen
import com.example.appmm_grupo5.ui.screens.EventosScreen
import com.example.appmm_grupo5.ui.screens.HomeScreenAdaptable
import com.example.appmm_grupo5.ui.screens.LoginScreen
import com.example.appmm_grupo5.ui.screens.PerfilScreen
import com.example.appmm_grupo5.ui.screens.RegistroScreen
import com.example.appmm_grupo5.viewmodel.MainViewModel
import com.example.appmm_grupo5.viewmodel.UsuarioViewModel
import kotlinx.coroutines.flow.collectLatest

// Graph de navegacion: une cada ruta screen con su correspondiente
// - NavController: lleva la pila de pantallas y los eventos de navegacion.
// - NavHost: muestra la pantalla que corresponde
@Composable
fun AppNavGraph(
    navController: NavHostController,
    viewModel: MainViewModel
) {
    // Un solo ViewModel de usuario compartido por Login y Registro
    val usuarioViewModel: UsuarioViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

    // Escucha los eventos de navegación que emite el ViewModel y los ejecuta en el NavController
    LaunchedEffect(key1 = Unit) {
        viewModel.navigationEvents.collectLatest { event ->
            when (event) {
                is NavigationEvent.NavigateTo -> {
                    navController.navigate(event.route.route) {
                        event.popUpToRoute?.let { destino ->
                            popUpTo(destino.route) { inclusive = event.inclusive }
                        }
                        launchSingleTop = event.singleTop
                        restoreState = true
                    }
                }

                is NavigationEvent.PopBackStack -> navController.popBackStack()
                is NavigationEvent.NavigateUp -> navController.navigateUp()
            }
        }
    }

    // TODO tarjeta 6: si hay una sesión guardada, empezar en Inicio
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(route = Screen.Login.route) {
            LoginScreen(
                viewModel = usuarioViewModel,
                onLoginExitoso = {
                    viewModel.navigateTo(Screen.Inicio, popUpTo = Screen.Login, inclusive = true)
                },
                onIrARegistro = { viewModel.navigateTo(Screen.Registro, singleTop = true) }
            )
        }
        composable(route = Screen.Registro.route) {
            RegistroScreen(
                viewModel = usuarioViewModel,
                onRegistroExitoso = {
                    viewModel.navigateTo(Screen.Inicio, popUpTo = Screen.Login, inclusive = true)
                },
                onVolver = { viewModel.navigateBack() }
            )
        }
        composable(route = Screen.Inicio.route) {
            HomeScreenAdaptable(
                onComenzar = { viewModel.navigateTo(Screen.Aprender) },
                onVerEventos = { viewModel.navigateTo(Screen.Eventos) },
                onIrA = { destino -> viewModel.navigateTo(destino, singleTop = true) }
            )
        }
        composable(route = Screen.Aprender.route) {
            AprenderScreen(viewModel = viewModel)
        }
        composable(route = Screen.Eventos.route) {
            EventosScreen(viewModel = viewModel)
        }
        composable(route = Screen.Perfil.route) {
            PerfilScreen(viewModel = viewModel)
        }
    }
}