package com.example.appmm_grupo5.repository

import com.example.appmm_grupo5.model.Postre
import com.example.appmm_grupo5.network.MealApiService
import com.example.appmm_grupo5.network.MealDto
import java.io.IOException

// Resultado de consultar la API externa
sealed class ResultadoApi<out T> {
    data class Exito<T>(val datos: T) : ResultadoApi<T>()
    object SinDatos : ResultadoApi<Nothing>()       // la API respondió "meals": null
    object ErrorRed : ResultadoApi<Nothing>()       // sin internet
    object ErrorServidor : ResultadoApi<Nothing>()  // la API respondió con error
}

// Repositorio de la API externa TheMealDB
class MealRepository(private val api: MealApiService) {

    suspend fun obtenerPostres(): ResultadoApi<List<Postre>> =
        try {
            val respuesta = api.postres("Dessert")
            if (respuesta.isSuccessful) {
                // meals puede venir null: se trata como lista vacía
                ResultadoApi.Exito(respuesta.body()?.meals.orEmpty().map { it.aPostre() })
            } else {
                ResultadoApi.ErrorServidor
            }
        } catch (e: IOException) {
            ResultadoApi.ErrorRed
        }

    suspend fun obtenerDetalle(id: String): ResultadoApi<Postre> =
        try {
            val respuesta = api.detalle(id)
            if (respuesta.isSuccessful) {
                val dto = respuesta.body()?.meals?.firstOrNull()
                if (dto != null) ResultadoApi.Exito(dto.aPostre()) else ResultadoApi.SinDatos
            } else {
                ResultadoApi.ErrorServidor
            }
        } catch (e: IOException) {
            ResultadoApi.ErrorRed
        }

    private fun MealDto.aPostre() = Postre(
        id = idMeal,
        nombre = strMeal ?: "Sin nombre",
        imagen = strMealThumb,
        categoria = strCategory,
        area = strArea,
        instrucciones = strInstructions
    )
}