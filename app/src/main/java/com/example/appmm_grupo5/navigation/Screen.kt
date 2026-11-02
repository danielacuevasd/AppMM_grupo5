package com.example.appmm_grupo5.navigation

sealed class Screen(val route: String) {

    // Rutas simples sin argumentos, son un objeto unico
    data object Inicio : Screen("inicio")
    data object Aprender : Screen("aprender")
    data object Perfil : Screen("perfil")
    data object Login : Screen("login")
    data object Registro : Screen("registro")
    data object Encuesta : Screen("encuesta")
    data object Splash : Screen("splash")

    //Rutas con argumentos, son objetos con parametros, esto es para el catalogo
    data class Detalle(val contenidoId: String) : Screen("detalle/{contenidoId}") {
        fun buildRoute(): String = route.replace("{contenidoId}", contenidoId)
    }
}