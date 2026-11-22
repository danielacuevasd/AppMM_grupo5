package com.example.appmm_grupo5.viewmodel

import com.example.appmm_grupo5.navigation.NavigationEvent
import com.example.appmm_grupo5.navigation.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

// Comprueba que el ViewModel emite los eventos de navegación que escucha el NavGraph
@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    @Before
    fun preparar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    @Test
    fun navigateTo_emite_el_evento_con_sus_datos() = runTest {
        val vm = MainViewModel()
        val eventos = mutableListOf<NavigationEvent>()
        val escucha = launch(UnconfinedTestDispatcher(testScheduler)) {
            vm.navigationEvents.toList(eventos)
        }

        vm.navigateTo(Screen.Perfil, singleTop = true, popUpTo = Screen.Inicio, inclusive = true)

        assertEquals(
            listOf<NavigationEvent>(NavigationEvent.NavigateTo(Screen.Perfil, Screen.Inicio, true, true)),
            eventos
        )
        escucha.cancel()
    }

    @Test
    fun navigateTo_sin_opciones_usa_los_valores_por_defecto() = runTest {
        val vm = MainViewModel()
        val eventos = mutableListOf<NavigationEvent>()
        val escucha = launch(UnconfinedTestDispatcher(testScheduler)) {
            vm.navigationEvents.toList(eventos)
        }

        vm.navigateTo(Screen.Aprender)

        assertEquals(
            listOf<NavigationEvent>(NavigationEvent.NavigateTo(Screen.Aprender, null, false, false)),
            eventos
        )
        escucha.cancel()
    }

    @Test
    fun navigateBack_emite_PopBackStack() = runTest {
        val vm = MainViewModel()
        val eventos = mutableListOf<NavigationEvent>()
        val escucha = launch(UnconfinedTestDispatcher(testScheduler)) {
            vm.navigationEvents.toList(eventos)
        }

        vm.navigateBack()

        assertEquals(listOf<NavigationEvent>(NavigationEvent.PopBackStack), eventos)
        escucha.cancel()
    }

    @Test
    fun navigateUp_emite_NavigateUp() = runTest {
        val vm = MainViewModel()
        val eventos = mutableListOf<NavigationEvent>()
        val escucha = launch(UnconfinedTestDispatcher(testScheduler)) {
            vm.navigationEvents.toList(eventos)
        }

        vm.navigateUp()

        assertEquals(listOf<NavigationEvent>(NavigationEvent.NavigateUp), eventos)
        escucha.cancel()
    }
}