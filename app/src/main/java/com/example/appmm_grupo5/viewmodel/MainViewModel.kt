package com.example.appmm_grupo5.viewmodel

import androidx.lifecycle.ViewModel
import com.example.appmm_grupo5.navigation.NavigationEvent
import com.example.appmm_grupo5.navigation.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

// ViewModel que trabaja en conjunto con el NavigationEvent
class MainViewModel : ViewModel() {

    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()

    val navigationEvents: SharedFlow<NavigationEvent> = _navigationEvents.asSharedFlow()

    fun navigateTo(screen: Screen, singleTop: Boolean = false) {
        CoroutineScope(Dispatchers.Main).launch {
            _navigationEvents.emit(NavigationEvent.NavigateTo(route = screen, singleTop = singleTop))
        }
    }

    // Para ir hacia abajo o aotras
    fun navigateBack() {
        CoroutineScope(Dispatchers.Main).launch {
            _navigationEvents.emit(NavigationEvent.PopBackStack)
        }
    }

    // Para ir hacia arriba
    fun navigateUp() {
        CoroutineScope(Dispatchers.Main).launch {
            _navigationEvents.emit(NavigationEvent.NavigateUp)
        }
    }
}