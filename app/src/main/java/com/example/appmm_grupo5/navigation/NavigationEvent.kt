package com.example.appmm_grupo5.navigation

// Las pantallas consultan el ViewModel para obtener datos y manejar la navegacion.
sealed class NavigationEvent {


    data class NavigateTo(
        val route: Screen,
        val popUpToRoute: Screen? = null,
        val inclusive: Boolean = false,
        val singleTop: Boolean = false
    ) : NavigationEvent()

    //Para ir bajar un nivel
    object PopBackStack : NavigationEvent()

    // Evento para subir un nivel
    object NavigateUp : NavigationEvent()
}