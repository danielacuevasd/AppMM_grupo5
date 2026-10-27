package com.example.appmm_grupo5.navigation

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.appmm_grupo5.viewmodel.SesionViewModel
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.appmm_grupo5.ui.screens.AprenderScreen
import com.example.appmm_grupo5.ui.screens.EncuestaScreen
import com.example.appmm_grupo5.ui.screens.EventosScreen
import com.example.appmm_grupo5.ui.screens.HomeScreenAdaptable
import com.example.appmm_grupo5.ui.screens.LoginScreen
import com.example.appmm_grupo5.ui.screens.PerfilScreen
import com.example.appmm_grupo5.ui.screens.RegistroScreen
import com.example.appmm_grupo5.ui.screens.SplashScreen
import com.example.appmm_grupo5.viewmodel.MainViewModel
import com.example.appmm_grupo5.viewmodel.PerfilViewModel
import com.example.appmm_grupo5.viewmodel.UsuarioViewModel
import kotlinx.coroutines.flow.collectLatest

// Graph de navegacion une cada ruta screen con su correspondiente
// - NavController: lleva las pantallas y los eventos de navegacion.
// - NavHost: muestra la pantalla que corresponde
@Composable
fun AppNavGraph(
    navController: NavHostController,
    viewModel: MainViewModel
) {
    // Un solo ViewModel por Login y Registro
    val usuarioViewModel: UsuarioViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

    // ViewModel para el perfil del negocio y la encuesta
    val perfilViewModel: PerfilViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

    // ViewModel de la sesion la cual recuerda si hay una sesion guardada
    val sesionViewModel: SesionViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val sesionActiva by sesionViewModel.sesionActiva.collectAsState()

    // Escucha los eventos que emite el ViewModel y los ejecuta en el NavController
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

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        // Transiciones entre pantallas:
        // al avanzar, la nueva entra desde la derecha y la anterior se desvanece;
        enterTransition = {
            fadeIn(tween(300)) + slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Start,
                tween(300)
            )
        },
        exitTransition = {
            fadeOut(tween(300)) + slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Start,
                tween(300),
                targetOffset = { it / 4 }
            )
        },
        popEnterTransition = {
            fadeIn(tween(300)) + slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.End,
                tween(300),
                initialOffset = { it / 4 }
            )
        },
        popExitTransition = {
            fadeOut(tween(300)) + slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.End,
                tween(300)
            )
        }
    ) {
        composable(
            route = Screen.Splash.route,
            exitTransition = { fadeOut(tween(400)) }
        ) {
            // El splash termina su animación, pero antes de salir hay que saber si hay sesión guardada
            var animacionTerminada by remember { mutableStateOf(false) }

            LaunchedEffect(animacionTerminada, sesionActiva) {
                if (animacionTerminada && sesionActiva != null) {
                    // Con sesión guardada se va directo a Inicio; sin sesión, al Login
                    val destino = if (sesionActiva == true) Screen.Inicio else Screen.Login
                    viewModel.navigateTo(destino, popUpTo = Screen.Splash, inclusive = true)
                }
            }

            SplashScreen(onTerminar = { animacionTerminada = true })
        }
        composable(
            route = Screen.Login.route,
            enterTransition = { fadeIn(tween(400)) }
        ) {
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
            PerfilScreen(
                viewModel = viewModel,
                perfilViewModel = perfilViewModel,
                onIrAEncuesta = { viewModel.navigateTo(Screen.Encuesta) }
            )
        }
        composable(route = Screen.Encuesta.route) {
            EncuestaScreen(
                perfilViewModel = perfilViewModel,
                onVolver = { viewModel.navigateBack() }
            )
        }
    }
}